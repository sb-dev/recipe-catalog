package com.recipeCatalog.config

import com.recipeCatalog.common.config.{ApplicationConfig, MongoConfig}
import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

trait AppConfig {
  def config: Config
  def applicationConfig: ApplicationConfig = config.as[ApplicationConfig]("app")
}

class RecipeCatalogConfig(val config: Config) extends AppConfig {
  val mongoConfig: MongoConfig = config.as[MongoConfig]("app.mongo")
}

object RecipeCatalogConfig {
  def apply(config: Config): RecipeCatalogConfig = {
    new RecipeCatalogConfig(config)
  }
}
