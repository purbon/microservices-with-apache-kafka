package services

import bus.PaymentKafkaProducer
import javax.inject.{Inject, Singleton}
import models.{JsonWriterAndReadersModule, Payment}
import play.api.libs.json.Json

@Singleton
class KafkaService @Inject()(bus: PaymentKafkaProducer) extends  JsonWriterAndReadersModule {

  def publish(customerId: Int, payment: Payment): Boolean = {
    try {
      payment.id match {
        case Some(paymentID) => {
          val stringKey = s"${customerId}#${paymentID}"
          bus.send(stringKey, Json.toJson(payment).toString())
        }
      }
      true
    } catch {
      case e: Exception => {
        println(e)
        false
      }
    }
  }
}
