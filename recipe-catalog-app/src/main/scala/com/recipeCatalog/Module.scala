package com.recipeCatalog

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.recipeCatalog.common.repository.Mongo
import com.recipeCatalog.model.Author.authorCodecProvider
import com.recipeCatalog.model.Recipe.recipeCodecProvider
import com.recipeCatalog.repository.RecipeRepository
import com.recipeCatalog.routes.RecipeRoute
import com.recipeCatalog.service.RecipeService
import com.typesafe.config.{Config, ConfigFactory}
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY

import scala.concurrent.ExecutionContextExecutor

trait Module {
  import com.softwaremill.macwire._

  implicit val system = ActorSystem("recipe-catalog-app")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val codecRegistry: CodecRegistry = fromRegistries(
    fromProviders(recipeCodecProvider, authorCodecProvider),
    DEFAULT_CODEC_REGISTRY
  )

  object MongoFactory {
    def create(config: Config) = Mongo(
      config.getString("app.mongo.database"),
      config.getString("app.mongo.url"),
      codecRegistry
    )
  }

  val config: Config = ConfigFactory.load()
  lazy val mongo: Mongo = wireWith(MongoFactory.create _)

  lazy val recipeRepository = wire[RecipeRepository]
  lazy val recipeService= wire[RecipeService]
  lazy val recipeRoutes = wire[RecipeRoute]
}

object Module extends Module
