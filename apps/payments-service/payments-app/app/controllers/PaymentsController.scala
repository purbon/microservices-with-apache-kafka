package controllers

import javax.inject._
import models.{JsonWriterAndReadersModule, Payment}
import play.api.libs.json._
import play.api.mvc._
import services.{CustomerService, PaymentService}


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class PaymentsController @Inject()(cc: ControllerComponents,
                                   customerService: CustomerService,
                                   paymentService: PaymentService) extends AbstractController(cc)
  with JsonWriterAndReadersModule {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>

    val customers = Json.toJson(paymentService.list())
    Ok(customers)
  }

  def show(customerId: Int, paymentId: Int) = Action  {
    val payment = paymentService.show(customerId, paymentId)

    Ok(Json.toJson(payment))
  }



  def newPayment(customerId: Int) = Action {  implicit request: Request[AnyContent] =>

    val body: AnyContent          = request.body
    val jsonBody: Option[JsValue] = body.asJson

    jsonBody match {
      case Some(jsonPayload) => {
        val paymentData: JsResult[Payment] = Json.fromJson[Payment](jsonPayload)
        val created = paymentService.save(customerId, paymentData.get)
        Ok(s" new payment, created = ${created} ")
      }
      case None => {
        BadRequest
      }
    }

  }
}
