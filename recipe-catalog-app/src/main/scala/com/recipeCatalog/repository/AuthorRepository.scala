package com.recipeCatalog.repository

import com.recipeCatalog.common.repository.{Mongo, MongoRepository}
import com.recipeCatalog.model.Author
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Updates

import scala.concurrent.{ExecutionContext, Future}

class AuthorRepository(val mongo: Mongo)(implicit ec: ExecutionContext) extends MongoRepository[Author, Long] {
  override val collectionName: String = "author"

  def update(id:String, author: Author): Future[Option[Author]] = {
    val toUpdate: Bson = Updates.combine(
      Updates.set("name", author.name)
    )

    super.update(id, toUpdate)
  }
}
