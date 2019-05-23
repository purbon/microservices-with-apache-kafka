package services

import javax.inject.{Inject, Singleton}
import models.Customer

@Singleton
class CustomerService @Inject()(kafkaService: KafkaService, dbService: CustomerDBService) {

  def show(id: Int): Customer = dbService.get(id)


  def save(customer: Customer): Boolean = {
    dbService.save(customer)
    true
  }

  def list(): List[Customer] = dbService.list()

}
