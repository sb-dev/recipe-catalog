package com.recipeCatalog.repository

import com.recipeCatalog.RecipeCatalogApplication.log
import com.recipeCatalog.common.repository.{IdGenerator, Mongo, MongoRepository}
import com.recipeCatalog.model.Author
import org.mongodb.scala.BulkWriteResult
import org.mongodb.scala.model.Updates

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class AuthorRepository(val mongo: Mongo, val idGenerator: IdGenerator)(implicit ec: ExecutionContext) extends MongoRepository[Author, String] {
  override val collectionName: String = "author"

  override def insert(author: Author): Future[String] = {
    super.insert(author.copy(_id = idGenerator.generate))
  }

  def update(id:String, author: Author): Future[Option[Author]] = {
    super.update(id, getUpdates(author))
  }

  def upsertAuthors(authors: List[Author]): Future[BulkWriteResult] = {
    super.upsert(
      authors.map((author) => {
        (author._id, getUpdates(author))
      })
    )
  }

  def processData(lines: Try[Iterator[String]]) = lines match {
    case Success(lines) => {
      val authors = for {
        line <- lines if line.split(",")(0) != "Id"
      } yield Author(line.split(","))

      upsertAuthors(authors.toList) onComplete {
        case Success(result) =>
          if(result.wasAcknowledged()) println(s"Initial author data has been inserted/updated")
        case Failure(e) => {
          println(s"Unable to load initial author data: ${e.getMessage}")
          e.printStackTrace()
        }
      }
    }
    case Failure(ex) => {
      log.error(s"Unable to load initial author data...")
      ex.printStackTrace()
    }
  }

  private def getUpdates(author: Author) = {
    Updates.combine(
      Updates.set("_id", author._id),
      Updates.set("name", author.name)
    )
  }
}
