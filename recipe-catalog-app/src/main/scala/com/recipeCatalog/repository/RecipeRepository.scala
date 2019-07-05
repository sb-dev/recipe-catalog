package com.recipeCatalog.repository

import com.recipeCatalog.common.repository.{IdGenerator, Mongo, MongoRepository}
import com.recipeCatalog.model.Recipe
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.{equal, elemMatch}
import org.mongodb.scala.model.Updates

import scala.concurrent.{ExecutionContext, Future}

class RecipeRepository(val mongo: Mongo, val idGenerator: IdGenerator)(implicit ec: ExecutionContext) extends MongoRepository[Recipe, String] {
  override def collectionName: String = "recipes"

  private val recipeFieldMap: Map[String, String] = Map(
    "ingredients" -> "ingredients:_id"
  )

  override def insert(recipe: Recipe): Future[String] = {
    super.insert(recipe.copy(_id = idGenerator.generate))
  }

  def findByAuthor(authorId: String): Future[Seq[Recipe]] = {
    super.query(equal("authorId", authorId))
  }

  def queryRecipes(queryParams: Map[String, String]): Future[Seq[Recipe]] = {
    val filters: Set[Bson] = for {
      field <- queryParams.keySet
      value <- queryParams.values
    } yield elemMatch(recipeFieldMap(field).split(":")(0), Document(recipeFieldMap(field).split(":")(1) -> value))
    super.query(filters.toList: _*)
  }

  def update(id:String, recipe: Recipe): Future[Option[Recipe]] = {
    val toUpdate: Bson = Updates.combine(
      Updates.set("title", recipe.title)
    )

    super.update(id, toUpdate)
  }
}
