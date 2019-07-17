package com.recipeCatalog.repository

import com.recipeCatalog.common.repository.{IdGenerator, Mongo, MongoRepository}
import com.recipeCatalog.model.Recipe
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.{and, elemMatch, equal}
import org.mongodb.scala.model.Updates

import scala.concurrent.{ExecutionContext, Future}

class RecipeRepository(val mongo: Mongo, val idGenerator: IdGenerator)(implicit ec: ExecutionContext) extends MongoRepository[Recipe, String] {
  override def collectionName: String = "recipes"

  private val recipeFieldMap: Map[String, (String, String)] = Map(
    "ingredients" -> ("ingredients", "_id")
  )

  override def insert(recipe: Recipe): Future[String] = {
    super.insert(recipe.copy(_id = idGenerator.generate))
  }

  def findByAuthor(authorId: String): Future[Seq[Recipe]] = {
    super.query(equal("authorId", authorId))
  }

  def queryRecipes(queryParams: Set[(String, String)]): Future[Seq[Recipe]] = {
    val filters: List[Bson] = queryParams.toList.map {
      case (field, value) => buildFilter(field, value)
    }
    super.query(and(filters: _*))
  }

  def update(id:String, recipe: Recipe): Future[Option[Recipe]] = {
    val toUpdate: Bson = Updates.combine(
      Updates.set("title", recipe.title)
    )

    super.update(id, toUpdate)
  }

  private def buildFilter(field: String, value: String): Bson = {
    if (recipeFieldMap.contains(field)) {
      val (nestedDocuments, filterField) = recipeFieldMap(field)
      elemMatch(nestedDocuments, Document(filterField -> value))
    } else {
      equal(field, value)
    }
  }
}
