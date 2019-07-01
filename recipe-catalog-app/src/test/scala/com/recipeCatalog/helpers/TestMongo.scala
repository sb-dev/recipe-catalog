package com.recipeCatalog.helpers

import java.util.UUID.randomUUID

import com.github.simplyscala.MongoEmbedDatabase
import com.recipeCatalog.common.repository.Mongo
import de.flapdoodle.embed.mongo.distribution.Version
import org.bson.codecs.configuration.{CodecRegistry}

import scala.util.Random

object TestMongo extends MongoEmbedDatabase {
  def apply(codecRegistry: CodecRegistry): Mongo = {
    val mongoPort = 12345 + Random.nextInt(1000)
    mongoStart(port = mongoPort, version = Version.V3_5_1)
    new Mongo("" +
      "RECIPE_CATALOG_" + randomUUID.toString,
      s"mongodb://localhost:$mongoPort",
      codecRegistry
    )
  }
}
