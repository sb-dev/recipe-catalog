package com.recipeCatalog.routes

import com.recipeCatalog.helpers.RouteSpec
import com.recipeCatalog.model.Author
import org.bson.types.ObjectId
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.{InsertOneModel, WriteModel}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

class AuthorRouteSpec extends RouteSpec with BeforeAndAfterAll with BeforeAndAfterEach  {
  val mongo = module.mongo
  val collection: MongoCollection[Author] = mongo.mongoDatabase.getCollection("author")
  val testAuthors = List(
    Author(
       new ObjectId(),
      "Test Author 1"
    )
  )

  override def beforeAll(): Unit = {
    val writes: List[WriteModel[Author]] = testAuthors.map(item => {
      InsertOneModel(item)
    })
    collection.bulkWrite(writes)

    super.beforeAll()
  }

  "Author API" should {
    "successfully return authors" in {
      Get("/api/authors") ~> module.authorRoutes.routes ~> check {
        responseAs[String] shouldEqual s""""[{"id":"${testAuthors(0)._id}",name:"${testAuthors(0).name}"]"""
      }
    }
  }
}