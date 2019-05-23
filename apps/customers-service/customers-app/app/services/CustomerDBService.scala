package services

import javax.inject.Singleton
import models.{Customer, PaymentWithID}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

@Singleton
class CustomerDBService {

  private var customerIndex:mutable.HashMap[Int, Int] = new mutable.HashMap[Int, Int]()
  private var customers: ArrayBuffer[Customer] = ArrayBuffer[Customer]()

  def get(id: Int): Customer = customerIndex.get(id) match {
    case Some(customerId) => customers(customerId)
    case None => throw new IllegalArgumentException("Invalid Id")
  }

  def list(): List[Customer] = customers.toList

  def save(customer: Customer): Boolean = {
    synchronized  {
      val listPos: Int = customerIndex.get(customer.id) match {
        case Some(arrayIndex) => arrayIndex
        case None => {
          val pos = customers.size
          customerIndex.put(customer.id, pos)
          pos
        }
      }
      if (listPos != customers.size) {
        customers.update(listPos, customer)
      } else {
        customers.append(customer)
      }
    }
    true
  }

  def update(customerId: Int, payments: List[PaymentWithID]): Unit = {

    customerIndex.get(customerId).map { listPos: Int =>
      synchronized {
        val customer = customers(listPos)
        customers(listPos) = customer.copy(payments = payments.map(_.asPayment))
      }
    }
  }

}
