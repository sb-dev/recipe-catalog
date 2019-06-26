package com.recipeCatalog.common.repository

import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.{InsertOneModel, WriteModel}
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

class Mongo(databaseName: String, url: String, codecRegistry: CodecRegistry) {
  val mongoDatabase: MongoDatabase = MongoClient(url).getDatabase(databaseName).withCodecRegistry(codecRegistry)
}

object Mongo {
  def apply(databaseName: String, url: String, codecRegistry: CodecRegistry): Mongo =
    new Mongo(databaseName, url, codecRegistry)
}

abstract class MongoRepository[A, IdType](implicit ec: ExecutionContext, ct: ClassTag[A]) {
  def mongo: Mongo
  def collectionName: String
  def collection: MongoCollection[A] = mongo.mongoDatabase.getCollection(collectionName)

  def queryAll(): Future[Seq[A]] = {
    collection.find().collect[A]().head()
  }

  def query(id: String): Future[Option[A]] = {
    collection.find(equal("_id", id)).first().head().map(Option(_))
  }

  def insert(a: A) {
    collection.insertOne(a)
  }

  def insert(a: List[A]) {
    val writes: List[WriteModel[_ <: A]] = a.map(item => {
      InsertOneModel(item)
    })
    collection.bulkWrite(writes)
  }
}
