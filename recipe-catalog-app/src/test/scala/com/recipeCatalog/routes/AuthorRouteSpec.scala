package com.recipeCatalog.routes

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{MessageEntity, StatusCodes}
import com.recipeCatalog.helpers.RouteSpec
import com.recipeCatalog.model.Author
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.{InsertOneModel, WriteModel}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

class AuthorRouteSpec extends RouteSpec with BeforeAndAfterAll with BeforeAndAfterEach with ScalaFutures with IntegrationPatience {
  val mongo = module.mongo
  val idGenerator = module.idGenerator
  val collection: MongoCollection[Author] = mongo.mongoDatabase.getCollection("author")
  val testAuthors = List(
    Author(
      idGenerator.generate,
      "Test Author 1"
    )
  )

  val authorEntity = Marshal(testAuthors(0)).to[MessageEntity].futureValue

  override def beforeAll(): Unit = {
    val writes: List[WriteModel[Author]] = testAuthors.map(item => {
      InsertOneModel(item)
    })
    collection.bulkWrite(writes).head().futureValue

    super.beforeAll()
  }

  "Author API" should {
    "successfully get authors" in {
      Get("/api/authors") ~> module.authorRoutes.routes ~> check {
        responseAs[Seq[Author]] should contain allElementsOf testAuthors
      }
    }

    "successfully create authors" in {
      Post("/api/authors").withEntity(authorEntity) ~> module.authorRoutes.routes ~> check {
        status shouldEqual StatusCodes.OK
      }
    }
  }
}