package com.recipeCatalog.common.repository

import com.mongodb.client.result.DeleteResult
import com.recipeCatalog.common.model.Entity
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.{and, equal}
import org.mongodb.scala.model.{InsertOneModel, UpdateOneModel, UpdateOptions}
import org.mongodb.scala.{BulkWriteResult, MongoClient, MongoCollection, MongoDatabase}

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
  def idGenerator: IdGenerator
  def collectionName: String
  def collection: MongoCollection[A] = mongo.mongoDatabase.getCollection(collectionName)

  def queryAll(): Future[Seq[A]] = {
    collection.find().collect[A]().head()
  }

  def query(id: String): Future[Option[A]] = {
    collection.find(equal("_id", id))
      .first()
      .head()
      .map(Option(_))
  }

  def query(filters: Bson*): Future[Seq[A]] = {
    val conditions: Bson = and(filters: _*)
    collection.find(conditions)
      .collect[A]()
      .head()
  }

  def insert(a: A): Future[String] = {
    collection
      .insertOne(a)
      .head()
      .map{ _ => a._id}
  }

  def update(id: String, updates: Bson): Future[Option[A]] = {
    collection
      .findOneAndUpdate(equal("_id", id), updates)
      .head()
      .map(Option(_))
  }

  def insert(a: List[A]) {
    collection.bulkWrite(a.map(InsertOneModel(_)))
  }

  def upsert(a: List[(String, Bson)]): Future[BulkWriteResult] = {
    collection.bulkWrite(
      for {
        (id, updates) <- a
      } yield UpdateOneModel(
        Document("_id" -> id),
        updates,
        UpdateOptions().upsert(true)
      )
    ).head()
  }

  def delete(id: String): Future[DeleteResult] = {
    collection.deleteOne(equal("_id", id))
      .head()
  }
}
