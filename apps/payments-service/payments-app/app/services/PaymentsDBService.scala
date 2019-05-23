package services

import javax.inject.Singleton
import models.{Customer, Payment}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

@Singleton
class PaymentsDBService {

  private var customerIndex:mutable.HashMap[Int, Int] = new mutable.HashMap[Int, Int]()
  private var customers: ArrayBuffer[Customer] = ArrayBuffer[Customer]()

  private var payments:mutable.HashMap[Int, ArrayBuffer[Payment]] = new mutable.HashMap[Int, ArrayBuffer[Payment]]()
  private var paymentsIndex:mutable.HashMap[Int, mutable.HashMap[Int, Int]] = new mutable.HashMap[Int, mutable.HashMap[Int, Int]]()

  def get(customerId: Int, paymentId: Int): Payment = paymentsIndex.get(customerId) match {
    case Some(paymentsIndex) => {
      paymentsIndex.get(paymentId) match {
        case Some(id) => payments.get(customerId).get(id)
        case None => throw new IllegalArgumentException("Invalid paymentsID")
      }
    }
    case None => throw new IllegalArgumentException("Invalid customerID")
  }

  def list(): mutable.Map[Int, ArrayBuffer[Payment]] = payments.clone()

  def save(customerId: Int, payment: Payment): Option[Payment] = {
    synchronized {
      val listPos: Int = paymentsIndex.get(customerId) match {
        case Some(customerPaymentIndex) => {
          val paymentId = payment.id.getOrElse(-1)
          customerPaymentIndex.get(paymentId) match {
            case Some(paymentId) => paymentId
            case None => {
              val pos = payments.get(customerId).size
              customerPaymentIndex.put(pos, pos)
              pos
            }
          }
        }
        case None => { // This is the first time a payment is added for this customer
          paymentsIndex.put(customerId, new mutable.HashMap[Int, Int]())
          payments.put(customerId, new ArrayBuffer[Payment]())
          0
        }
      }

      val customerPaymentsArray = payments.get(customerId).get
      val savedPayment = payment.copy(id = Some(listPos))
      if (listPos != customerPaymentsArray.size) {
        payments.get(customerId).get.update(listPos, savedPayment)
      } else {
        payments.get(customerId).get.append(savedPayment)
        paymentsIndex.get(customerId).get.put(listPos, listPos)
      }
      Some(savedPayment)
    }

  }
}
