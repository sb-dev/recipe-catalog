package com.recipeCatalog

import akka.event.Logging
import akka.http.scaladsl.Http

import scala.util.{Failure, Success, Try}

object Application extends App {
  import Module._
  val log = Logging.getLogger(system, this)

  val routes = recipeRoutes.recipeRoutes
  val host: String = "0.0.0.0"
  val appPort: Int = Try(config.getInt("app.port")).getOrElse(8080)

  Http().bindAndHandle(routes, host, appPort).onComplete {
    case Success(b) => log.info(s"Application is up and running at ${b.localAddress.getHostName}:${b.localAddress.getPort}")
    case Failure(e) => log.error(s"could not start application: {}", e.getMessage)
  }
}