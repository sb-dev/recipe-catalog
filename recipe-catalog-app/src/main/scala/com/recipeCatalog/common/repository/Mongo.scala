package com.recipeCatalog.common.repository

import com.mongodb.client.result.DeleteResult
import com.recipeCatalog.common.model.Entity
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.BsonObjectId
import org.mongodb.scala.bson.conversions.Bson
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

abstract class MongoRepository[A <: Entity, IdType](implicit ec: ExecutionContext, ct: ClassTag[A]) {
  def mongo: Mongo
  def collectionName: String
  def collection: MongoCollection[A] = mongo.mongoDatabase.getCollection(collectionName)

  def queryAll(): Future[Seq[A]] = {
    collection.find().collect[A]().head()
  }

  def query(id: String): Future[Option[A]] = {
    collection.find(equal("_id", BsonObjectId(id)))
      .first()
      .head()
      .map(Option(_))
  }

  def query(filter: Bson): Future[Seq[A]] = {
    collection.find(filter)
      .collect[A]()
      .head()
  }

  def insert(a: A): Future[String] = {
    collection
      .insertOne(a)
      .head()
      .map{ _ => a._id.toHexString}
  }

  def update(id: String, updates: Bson): Future[Option[A]] = {
    collection
      .findOneAndUpdate(equal("_id", BsonObjectId(id)), updates)
      .head()
      .map(Option(_))
  }

  def insert(a: List[A]) {
    val writes: List[WriteModel[_ <: A]] = a.map(item => {
      InsertOneModel(item)
    })
    collection.bulkWrite(writes)
  }

  def delete(id: String): Future[DeleteResult] = {
    collection.deleteOne(equal("_id", BsonObjectId(id)))
      .head()
  }
}
