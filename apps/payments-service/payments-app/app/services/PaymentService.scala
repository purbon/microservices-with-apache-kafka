package services

import javax.inject.{Inject, Singleton}
import models.Payment

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

@Singleton
class PaymentService @Inject()(kafkaService: KafkaService, dbService: PaymentsDBService) {

  def show(customerId: Int, paymentId: Int): Payment = dbService.get(customerId, paymentId)


  def save(customerId: Int, payment: Payment): Boolean = {
    dbService.save( customerId = customerId, payment = payment).map { savedPayment =>
        kafkaService.publish(customerId, savedPayment)
    }

    true
  }

  def list(): mutable.Map[Int, ArrayBuffer[Payment]] = dbService.list()

}
