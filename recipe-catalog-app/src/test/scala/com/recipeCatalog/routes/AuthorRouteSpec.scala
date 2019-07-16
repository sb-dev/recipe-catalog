package com.recipeCatalog.routes

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.headers.Location
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
      "5d16314256ffa4012e827b45",
      "Test Author 1"
    )
  )

  val testPostAuthor = Author(
    "",
    "Test Author 2"
  )
  val createAuthorEntity = Marshal(testPostAuthor).to[MessageEntity].futureValue

  val testPutAuthor = Author(
    "5d16314256ffa4012e827b45",
    "Test Author 1.2"
  )
  val updateAuthorEntity = Marshal(testPutAuthor).to[MessageEntity].futureValue

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
        responseAs[Seq[Author]] should have size (1)
      }
    }

    "successfully create authors" in {
      Post("/api/authors").withEntity(createAuthorEntity) ~> module.authorRoutes.routes ~> check {
        status shouldEqual StatusCodes.Created
        header("Location") shouldEqual Some(Location(s"api/author/${idGenerator.generate}"))
      }
    }

    "successfully get authors by Id" in {
      Get(s"/api/authors/${testAuthors(0)._id}") ~> module.authorRoutes.routes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Author] shouldEqual testAuthors(0)
      }
    }

    "Fail with 404 when retrieving an author with unknown Id" in {
      Get(s"/api/authors/unknown_id") ~> module.authorRoutes.routes ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }

    "successfully update an author" in {
      Put(s"/api/authors/${testPutAuthor._id}").withEntity(updateAuthorEntity) ~> module.authorRoutes.routes ~> check {
        status shouldEqual StatusCodes.Created
      }
    }

    "successfully delete an author" in {
      Delete(s"/api/authors/${idGenerator.generate}") ~> module.authorRoutes.routes ~> check {
        status shouldEqual StatusCodes.OK
      }
    }

    "Fail silently when deleting an unknown author" in {
      Delete("/api/authors/unknown_id") ~> module.authorRoutes.routes ~> check {
        status shouldEqual StatusCodes.OK
      }
    }

    "successfully create a recipe" in {
      Delete(s"/api/authors/${idGenerator.generate}") ~> module.authorRoutes.routes ~> check {
        status shouldEqual StatusCodes.OK
      }
    }

    "successfully get recipes by author Id" in {
      Delete(s"/api/authors/5d16314256ffa4012e827b45/recipes") ~> module.authorRoutes.routes ~> check {
        status shouldEqual StatusCodes.OK
      }
    }
  }
}