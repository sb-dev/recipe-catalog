package com.recipeCatalog.repository

import com.recipeCatalog.common.repository.{Mongo, MongoRepository}
import com.recipeCatalog.model.Recipe
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates

import scala.concurrent.{ExecutionContext, Future}

class RecipeRepository(val mongo: Mongo)(implicit ec: ExecutionContext) extends MongoRepository[Recipe, String] {
  override def collectionName: String = "recipes"

  def findByAuthor(authorId: String): Future[Seq[Recipe]] = {
    super.query(equal("authorId", authorId))
  }

  def update(id:String, recipe: Recipe): Future[Recipe] = {
    val toUpdate: Bson = Updates.combine(
      Updates.set("title", recipe.title)
    )

    super.update(id, toUpdate)
  }
}
