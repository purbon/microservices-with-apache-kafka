package events

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

object ConsumerKafkaProducer {
  val TOPIC = "customers"
}
class ConsumerKafkaProducer {

  private val producer: KafkaProducer[Int, String] = {
    new KafkaProducer[Int, String](configure())
  };

  def send(customerId: Int, payload: String): Unit = {
    val record: ProducerRecord[Int, String] = new ProducerRecord[Int, String](ConsumerKafkaProducer.TOPIC, customerId, payload)
    producer.send(record)
  }

  private def configure(): Properties = {
    val props = new Properties
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "ConsumerProducer")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerSerializer")
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true")
    props
  }

}
