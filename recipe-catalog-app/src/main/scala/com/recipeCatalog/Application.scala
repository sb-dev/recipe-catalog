package com.recipeCatalog

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.DebuggingDirectives
import akka.stream.ActorMaterializer
import com.recipeCatalog.common.config.ApplicationConfig
import com.typesafe.config.{Config, ConfigFactory}
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

trait Application extends App {
  val config: Config = ConfigFactory.load()
  val applicationConfig: ApplicationConfig = config.as[ApplicationConfig]("app")
  val name: String = applicationConfig.name
  val host: String = "0.0.0.0"
  val appPort: Int = applicationConfig.port

  implicit val system = ActorSystem(name)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val log = Logging.getLogger(system, this)
  def startApplication(routes: Route): Unit = {
    val loggedRoutes = DebuggingDirectives.logRequestResult("REST API", Logging.InfoLevel)(routes)
    Http().bindAndHandle(loggedRoutes, host, appPort).onComplete {
      case Success(b) => log.info(s"Connecting to ${config.getString("app.mongo.url")}");log.info(s"Application is up and running at ${b.localAddress.getHostName}:${b.localAddress.getPort}")
      case Failure(e) => log.error(s"could not start application: {}", e.getMessage)
    }
  }
}
