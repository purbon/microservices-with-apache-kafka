package controllers

import javax.inject._
import models.{Customer, JsonWriterAndReadersModule}
import play.api.libs.json.JsValue
import play.api.mvc._
import services.CustomerService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class CustomersController @Inject()(cc: ControllerComponents,
                                    customerService: CustomerService
                                   ) extends AbstractController(cc) with JsonWriterAndReadersModule {


  def index(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    customerService.list().map { response =>
      Ok(response.body)
    }
  }

  def show(id: Int): Action[AnyContent] = Action.async  {
    customerService.show(id).map { response =>
      Ok(response.body)
    }
  }

  def newCustomer(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    if (request.hasBody) {
      request.body.asJson match {
        case Some(value: JsValue) => {
          customerService.save(value.as[Customer])
          Future.successful(Ok(s" new customer created"))
        }
        case _ => {
          Future.successful(BadRequest("Wrong JSON format"))
        }
      }
    } else {
      Future.successful(BadRequest)
    }
  }

}
