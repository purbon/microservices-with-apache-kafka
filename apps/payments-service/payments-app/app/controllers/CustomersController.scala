package controllers

import javax.inject._
import models.{Customer, JsonWriterAndReadersModule}
import play.api.libs.json._
import play.api.mvc._
import services.CustomerService


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class CustomersController @Inject()(cc: ControllerComponents,
                                    customerService: CustomerService) extends AbstractController(cc) with JsonWriterAndReadersModule {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>

    val customers = Json.toJson(customerService.list())
    Ok(customers)
  }

  def show(id: Int) = Action  {
    val customer = customerService.show(id)
    Ok(Json.toJson(customer))
  }



  def newCustomer() = Action {  implicit request: Request[AnyContent] =>

    val body: AnyContent          = request.body
    val jsonBody: Option[JsValue] = body.asJson

    jsonBody match {
      case Some(jsonPayload) => {
        val customerData: JsResult[Customer] = Json.fromJson[Customer](jsonPayload)
        val created = customerService.save(customerData.get)
        Ok(s" new customer, created = ${created} ")
      }
      case None => {
        BadRequest
      }
    }

  }
}
