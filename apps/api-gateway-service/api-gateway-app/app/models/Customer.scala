package models

case class Payment(`type`: String, code: String)
case class PaymentWithID(id: Int, `type`: String, code: String) {
  def asPayment: Payment  = Payment(`type`, code)
}
case class Customer(id: Int=0, name: String, familyName: String, country: String, payments: List[Payment])
