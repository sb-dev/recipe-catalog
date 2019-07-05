package com.recipeCatalog

import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.DebuggingDirectives
import com.recipeCatalog.swagger.SwaggerDocService

import scala.util.{Failure, Success, Try}

object Application extends App {
  import Module._
  val log = Logging.getLogger(system, this)

  val swaggerUiRoute = getFromResourceDirectory("swagger-ui")

  val routes: Route = recipeRoutes.routes ~ authorRoutes.routes ~ SwaggerDocService.routes ~ swaggerUiRoute
  val host: String = "0.0.0.0"
  val appPort: Int = Try(config.getInt("app.port")).getOrElse(8080)

  val loggedRoutes = DebuggingDirectives.logRequestResult("Client ReST", Logging.InfoLevel)(routes)

  Http().bindAndHandle(loggedRoutes, host, appPort).onComplete {
    case Success(b) => log.info(s"Application is up and running at ${b.localAddress.getHostName}:${b.localAddress.getPort}")
    case Failure(e) => log.error(s"could not start application: {}", e.getMessage)
  }
}