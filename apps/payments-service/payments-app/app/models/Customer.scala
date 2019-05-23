package models

case class Customer(id: Int=0, name: String, familyName: String, country: String, payments: List[Payment])
