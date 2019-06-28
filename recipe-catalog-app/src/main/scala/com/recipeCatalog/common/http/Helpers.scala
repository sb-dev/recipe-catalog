package com.recipeCatalog.common.http

import akka.http.scaladsl.marshalling.{Marshal, ToResponseMarshallable}
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.model.{HttpResponse, ResponseEntity, StatusCodes}
import akka.stream.Materializer
import com.recipeCatalog.common.model.Message
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._

import scala.concurrent.{ExecutionContext, Future}

object Helpers {
    implicit final def handleFailure(message: String)(implicit ec: ExecutionContext, mat: Materializer): Future[HttpResponse] = {
      Marshal(Message(message)).to[ResponseEntity].map{
        e => HttpResponse(entity = e, status = StatusCodes.InternalServerError)
      }
    }

    implicit final def handleNotFound:HttpResponse = {
      HttpResponse(status = StatusCodes.NotFound)
    }

  implicit final def handleCreated(resourceName: String, resourceId: String): HttpResponse = {
    HttpResponse(status = StatusCodes.Created, headers = List(Location(s"api/${resourceName}/${resourceId}")))
  }

  implicit final def handleSuccess: HttpResponse = {
    HttpResponse(status = StatusCodes.OK)
  }
}
