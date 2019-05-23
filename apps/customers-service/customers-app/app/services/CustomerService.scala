package services

import javax.inject.{Inject, Singleton}
import models.{Customer, Payment}
import play.api.libs.ws.WSResponse

import scala.concurrent.Future

@Singleton
class CustomerService @Inject()(kafkaService: KafkaService,
                                dbService: CustomerDBService,
                                paymentsService: PaymentsService) {

  import scala.concurrent.ExecutionContext.Implicits.global

  def show(id: Int): Customer = dbService.get(id)


  def save(aggCustomer: Customer): Future[List[WSResponse]] = {

    val customer = aggCustomer.copy(payments = List.empty[Payment])
    dbService.save(customer)

    // publish customer information, for other services to operate
    kafkaService.publish(customer)

    // send payment request to the payment service
    val customerPayments = aggCustomer.payments
    Future.sequence {
      customerPayments.map { payment =>
        paymentsService.save(customer, payment)
      }
    }
    // END
  }

  def list(): List[Customer] = dbService.list()

}
