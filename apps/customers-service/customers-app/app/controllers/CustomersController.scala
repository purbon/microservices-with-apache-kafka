package controllers

import javax.inject._
import models.{Customer, JsonWriterAndReadersModule}
import play.api.libs.json._
import play.api.libs.ws.WSResponse
import play.api.mvc._
import services.CustomerService

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

@Singleton
class CustomersController @Inject()(cc: ControllerComponents,
                                    customerService: CustomerService
                                   ) extends AbstractController(cc) with JsonWriterAndReadersModule {


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
        val requests: Future[List[WSResponse]] = customerService.save(customerData.get)

        Await.result(requests, Duration.Inf)

        Ok(s" new customer, created = true")
      }
      case None => {
        BadRequest
      }
    }

  }
}
