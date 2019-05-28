package events

import java.time.Instant
import java.util.Properties

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import models.{Customer, PaymentWithID}
import org.apache.kafka.common.serialization.{Serde, Serdes}
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams._
import org.apache.kafka.streams.kstream._
import org.apache.kafka.streams.state.KeyValueStore
import play.api.Logger
import play.api.inject.ApplicationLifecycle
import serdes.{JsonPOJODeserializer, JsonPOJOSerializer}
import services.CustomerDBService

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

@Singleton
class PaymentEventHandlerTask @Inject()(lifecycle: ApplicationLifecycle,
                                        actorSystem: ActorSystem,
                                        customerDBService: CustomerDBService)(implicit executionContext: ExecutionContext) {

  val EVENT_HANDLER_ID: String = "PaymentsEventHandler"

  actorSystem.scheduler.scheduleOnce(delay = 10 seconds) {

    val streamsApp = PaymentsEventHandler.build(EVENT_HANDLER_ID, Some(customerDBService))
    lifecycle.addStopHook { () =>
      val clock = Instant.now()
      Logger.info(s"PaymentsEventHandler: Stopping the handler at ${clock}.")
      Future {
        streamsApp.close()
      }
    }
  }
}


case class AggregatedPaymentsTable() {

  var customerId = ""
  var list = List.empty[PaymentWithID]
  def addPayment(customerId: String, payment: PaymentWithID): AggregatedPaymentsTable = {
    this.customerId = customerId
    list = list.find(_.id == payment.id)  match {
      case Some(_) => {
        list
      }
      case None => {
        (list.++(List(payment)))
      }
    }
    this
  }

  override def toString: String = {
    s"customerId= ${customerId}, payments=${list.mkString(",")}"
  }
}

object PaymentsEventHandler {

  def main(args: Array[String]): Unit = {
    val streamsApp = build("dummy-app-test-event-handler")

    sys.addShutdownHook( {
      streamsApp.close()
    })

  }

  def build(clientId: String, customerDBService: Option[CustomerDBService] = None): KafkaStreams = {

    val builder = new StreamsBuilder

    val customerSerde: Serde[Customer] = Serdes.serdeFrom(new JsonPOJOSerializer[Customer](classOf[Customer]),
                                          new JsonPOJODeserializer[Customer](classOf[Customer]))

    val paymentsSerde: Serde[PaymentWithID] = Serdes.serdeFrom(new JsonPOJOSerializer[PaymentWithID](classOf[PaymentWithID]),
                                          new JsonPOJODeserializer[PaymentWithID](classOf[PaymentWithID]))

    val totalsSerde: Serde[AggregatedPaymentsTable] = Serdes.serdeFrom(
                new JsonPOJOSerializer[AggregatedPaymentsTable](classOf[AggregatedPaymentsTable]),
                new JsonPOJODeserializer[AggregatedPaymentsTable](classOf[AggregatedPaymentsTable])
    )

    val customers: KTable[String, Customer] = builder
      .table(ConsumerKafkaProducer.TOPIC, Consumed.`with`(Serdes.String, customerSerde))

    val payments  = builder
          .stream("payments", Consumed.`with`(Serdes.String, paymentsSerde))


    val groupedPayments: KTable[String, AggregatedPaymentsTable] =
      payments
        .groupBy(
          new KeyValueMapper[String, PaymentWithID, String] {
            override def apply(key: String, value: PaymentWithID): String =  key.split("#")(0)
          },
          Grouped.`with`(Serdes.String, paymentsSerde)
        )
       .aggregate(
          () => new AggregatedPaymentsTable(),
          (key: String, payment: PaymentWithID, aggregate: AggregatedPaymentsTable) => {
            println(payment)
            aggregate.addPayment(key, payment)
          },
         Materialized.`with`[String, AggregatedPaymentsTable, KeyValueStore[Bytes, Array[Byte]]](Serdes.String, totalsSerde)
        )

    /*
    [KTABLE-TOSTREAM-0000000007]: 0, customerId= 0, payments=PaymentWithID(1,iban,4321 3455)
    [KTABLE-TOSTREAM-0000000007]: 0, customerId= 0, payments=PaymentWithID(1,iban,4321 3455),PaymentWithID(0,cc,1234)
     */

    customerDBService match {
      case Some(service) => {
        groupedPayments
          .toStream
          .foreach((customerId: String, payments: AggregatedPaymentsTable) => {
            service.update(customerId.toInt, payments.list)
          })
      }
      case None => {
        groupedPayments
          .toStream
          .print(Printed.toSysOut[String, AggregatedPaymentsTable])
      }
    }

    run(builder.build(), clientId)
  }

  private def configure(clientId: String): Properties = {

    val config = new Properties

    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "PaymentsEventHandler")
    config.put(StreamsConfig.CLIENT_ID_CONFIG, clientId)

    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:29092")
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "100")
    config.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, "4")
    config
  }

  /**
    * Execution method for this event handler
    * @param topology
    * @param clientId
    * @return
    */
  private def run(topology: Topology, clientId: String): KafkaStreams = {

    val streams = new KafkaStreams(topology, configure(clientId))
    streams.cleanUp()
    streams.start()

    streams
  }
}
