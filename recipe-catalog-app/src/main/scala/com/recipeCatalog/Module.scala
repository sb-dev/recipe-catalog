package com.recipeCatalog

import akka.http.scaladsl.server.Directives.{getFromResourceDirectory, _}
import com.recipeCatalog.common.config.MongoConfig
import com.recipeCatalog.common.repository.{IdGenerator, Mongo}
import com.recipeCatalog.model.Author.authorCodecProvider
import com.recipeCatalog.model.Ingredient.ingredientCodecProvider
import com.recipeCatalog.model.Recipe.recipeCodecProvider
import com.recipeCatalog.repository.{AuthorRepository, RecipeRepository}
import com.recipeCatalog.routes.{AuthorRoute, RecipeRoute}
import com.recipeCatalog.service.{AuthorService, RecipeService}
import com.recipeCatalog.swagger.SwaggerDocService
import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY

trait Module extends Application {
  import com.softwaremill.macwire._

  val mongoConfig: MongoConfig = config.as[MongoConfig]("app.mongo")
  val codecRegistry: CodecRegistry = fromRegistries(
    fromProviders(recipeCodecProvider, authorCodecProvider, ingredientCodecProvider),
    DEFAULT_CODEC_REGISTRY
  )

  object MongoFactory {
    def create(config: Config) = Mongo(
      mongoConfig.database,
      mongoConfig.url,
      codecRegistry
    )
  }

  lazy val mongo: Mongo = wireWith(MongoFactory.create _)
  lazy val idGenerator: IdGenerator = IdGenerator

  lazy val recipeRepository = wire[RecipeRepository]
  lazy val recipeService= wire[RecipeService]
  lazy val recipeRoutes = wire[RecipeRoute]

  lazy val authorRepository = wire[AuthorRepository]
  lazy val authorService= wire[AuthorService]
  lazy val authorRoutes = wire[AuthorRoute]

  lazy val swaggerRoutes = SwaggerDocService.routes ~ getFromResourceDirectory("swagger-ui")
}
