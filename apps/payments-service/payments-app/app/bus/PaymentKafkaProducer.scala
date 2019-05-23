package bus

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

object PaymentKafkaProducer {
  val TOPIC = "payments"
}
class PaymentKafkaProducer {

  private val producer: KafkaProducer[String, String] = {
    new KafkaProducer[String, String](configure())
  };

  def send(messageKey: String, payload: String): Unit = {
    val record: ProducerRecord[String, String] = new ProducerRecord[String, String](PaymentKafkaProducer.TOPIC, messageKey, payload)
    producer.send(record)
  }

  private def configure(): Properties = {
    val props = new Properties
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "ConsumerProducer")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true")
    props
  }

}
