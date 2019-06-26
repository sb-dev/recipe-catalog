package com.recipeCatalog

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.recipeCatalog.common.repository.Mongo
import com.recipeCatalog.model.Author.authorCodecProvider
import com.recipeCatalog.model.Recipe.recipeCodecProvider
import com.recipeCatalog.routes.RecipeRoute
import com.typesafe.config.{Config, ConfigFactory}
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success, Try}

object BootApp extends App {
  implicit val system = ActorSystem("recipe-catalog-app")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val config: Config = ConfigFactory.load()

  val host: String = "0.0.0.0"
  val appPort: Int = Try(config.getInt("app.port")).getOrElse(8080)
  val codecRegistry: CodecRegistry = fromRegistries(fromProviders(recipeCodecProvider, authorCodecProvider), DEFAULT_CODEC_REGISTRY)
  val mongo: Mongo = Mongo(config.getString("app.mongo.database"), config.getString("app.mongo.url"), codecRegistry)

  val log = Logging.getLogger(system, this)

  val recipeRoutes = new RecipeRoute(mongo)

  val routes = recipeRoutes.recipeRoutes;

  Http().bindAndHandle(routes, host, appPort).onComplete {
    case Success(b) => log.info(s"Application is up and running at ${b.localAddress.getHostName}:${b.localAddress.getPort}")
    case Failure(e) => log.error(s"could not start application: {}", e.getMessage)
  }
}