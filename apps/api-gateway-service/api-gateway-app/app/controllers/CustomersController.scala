package controllers

import javax.inject._
import models.{Customer, JsonWriterAndReadersModule}
import play.api.libs.json._
import play.api.libs.ws.WSResponse
import play.api.mvc._
import services.CustomerService

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class CustomersController @Inject()(cc: ControllerComponents,
                                    customerService: CustomerService
                                   ) extends AbstractController(cc) with JsonWriterAndReadersModule {


  def index(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val customers: Future[WSResponse] = customerService.list()
    customers.map { response =>
      Ok(response.body)
    }
  }

  def show(id: Int): Action[AnyContent] = Action.async  {
    val customer: Future[WSResponse] = customerService.show(id)
    customer.map { response =>
      Ok(response.body)
    }

  }

  def newCustomer(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>

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
