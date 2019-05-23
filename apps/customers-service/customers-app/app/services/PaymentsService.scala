package services

import javax.inject.{Inject, Singleton}
import models.{Customer, JsonWriterAndReadersModule, Payment}
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.Future

@Singleton
class PaymentsService @Inject()(kafkaService: KafkaService, ws: WSClient) extends JsonWriterAndReadersModule {

  private val PAYMENTS_SERVICE: String = "http://localhost:9001"

  def show(id: Int): Payment = ???


  def save(customer: Customer, payment: Payment): Future[WSResponse] = {

    val paymentUrl = s"${PAYMENTS_SERVICE}/customers/${customer.id}/payments/new"
    ws.url(paymentUrl).post(Json.toJson(payment))
  }

  def list(): List[Payment] = ???

}
