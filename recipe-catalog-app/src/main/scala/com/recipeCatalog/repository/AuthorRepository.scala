package com.recipeCatalog.repository

import com.recipeCatalog.common.repository.{IdGenerator, Mongo, MongoRepository}
import com.recipeCatalog.model.Author
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Updates

import scala.concurrent.{ExecutionContext, Future}

class AuthorRepository(val mongo: Mongo, val idGenerator: IdGenerator)(implicit ec: ExecutionContext) extends MongoRepository[Author, Long] {
  override val collectionName: String = "author"

  override def insert(author: Author): Future[String] = {
    super.insert(author.copy(_id = idGenerator.generate))
  }

  def update(id:String, author: Author): Future[Option[Author]] = {
    val toUpdate: Bson = Updates.combine(
      Updates.set("name", author.name)
    )

    super.update(id, toUpdate)
  }
}
