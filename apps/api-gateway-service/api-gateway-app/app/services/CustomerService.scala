package services

import javax.inject.{Inject, Singleton}
import models.{Customer, JsonWriterAndReadersModule, Payment}
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.Future

@Singleton
class CustomerService @Inject()(paymentsService: PaymentsService, ws: WSClient) extends JsonWriterAndReadersModule {

  private val CUSTOMERS_SERVICE: String = "http://customers-service:9000"

  import scala.concurrent.ExecutionContext.Implicits.global

  def show(id: Int): Future[WSResponse] = {
    val serviceUrl = s"${CUSTOMERS_SERVICE}/customers/${id}"
    ws
      .url(serviceUrl)
      .get()
  }


  def save(aggCustomer: Customer): Future[List[WSResponse]] = {

    val customer = aggCustomer.copy(payments = List.empty[Payment])

    val newCustomerUrl = s"${CUSTOMERS_SERVICE}/customers/new"
    ws.url(newCustomerUrl).post(Json.toJson(customer))

    // send payment request to the payment service
    val customerPayments = aggCustomer.payments
    Future.sequence {
      customerPayments.map { payment =>
        paymentsService.save(customer, payment)
      }
    }
  }

  def list(): Future[WSResponse] = {
    val serviceUrl = s"${CUSTOMERS_SERVICE}/customers"

    ws
      .url(serviceUrl)
      .get
  }

}
