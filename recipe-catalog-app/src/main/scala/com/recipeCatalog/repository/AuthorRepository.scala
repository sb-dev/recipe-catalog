package com.recipeCatalog.repository

import com.recipeCatalog.common.repository.{Mongo, MongoRepository}
import com.recipeCatalog.model.Author

import scala.concurrent.ExecutionContext

class AuthorRepository(val mongo: Mongo)(implicit ec: ExecutionContext) extends MongoRepository[Author, Long] {
  override val collectionName: String = "author"
}
