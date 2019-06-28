package com.recipeCatalog

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.recipeCatalog.common.repository.Mongo
import com.recipeCatalog.model.Author.authorCodecProvider
import com.recipeCatalog.model.Recipe.recipeCodecProvider
import com.recipeCatalog.repository.{AuthorRepository, RecipeRepository}
import com.recipeCatalog.routes.{AuthorRoute, RecipeRoute}
import com.recipeCatalog.service.{AuthorService, RecipeService}
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

  val config: Config = ConfigFactory.load()
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

  lazy val mongo: Mongo = wireWith(MongoFactory.create _)

  lazy val recipeRepository = wire[RecipeRepository]
  lazy val recipeService= wire[RecipeService]
  lazy val recipeRoutes = wire[RecipeRoute]

  lazy val authorRepository = wire[AuthorRepository]
  lazy val authorService= wire[AuthorService]
  lazy val authorRoutes = wire[AuthorRoute]
}

object Module extends Module
