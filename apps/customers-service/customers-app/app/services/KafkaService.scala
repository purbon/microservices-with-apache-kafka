package services

import events.ConsumerKafkaProducer
import javax.inject.{Inject, Singleton}
import models.{Customer, JsonWriterAndReadersModule}
import play.api.libs.json.Json

@Singleton
class KafkaService @Inject()(bus: ConsumerKafkaProducer) extends  JsonWriterAndReadersModule {

  def publish(customer: Customer): Boolean = {
    try {
      bus.send(customer.id, Json.toJson(customer).toString())
      true
    } catch {
      case e: Exception => {
        println(e)
        false
      }
    }
  }
}
