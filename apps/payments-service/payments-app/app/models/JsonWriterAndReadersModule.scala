package models

import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{JsPath, Reads, Writes}
import play.api.libs.functional.syntax._

trait JsonWriterAndReadersModule {

  implicit val paymentWrites: Writes[Payment] = (
    (JsPath \ "id").writeOptionWithNull[Int] and
    (JsPath \ "type").write[String] and
      (JsPath \ "code").write[String]
    )(unlift(Payment.unapply))

  implicit val paymentReads: Reads[Payment] = (
    (JsPath \ "id").readNullable[Int] and
    (JsPath \ "type").read[String] and
      (JsPath \ "code").read[String]
    )(Payment.apply _)


  implicit val customerWrites: Writes[Customer] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "name").write[String] and
      (JsPath \ "familyName").write[String] and
      (JsPath \ "country").write[String] and
      (JsPath \ "payments").write[List[Payment]]
    )(unlift(Customer.unapply))

  implicit val customerReads: Reads[Customer] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "name").read[String] and
      (JsPath \ "familyName").read[String] and
      (JsPath \ "country").read[String] and
      (JsPath \ "payments").read[List[Payment]]
    )(Customer.apply _)
}
