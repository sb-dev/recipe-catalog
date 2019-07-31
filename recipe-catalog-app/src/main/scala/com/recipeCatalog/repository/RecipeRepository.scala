package com.recipeCatalog.repository

import com.recipeCatalog.common.repository.{IdGenerator, Mongo, MongoRepository}
import com.recipeCatalog.model.{Ingredient, Recipe}
import org.mongodb.scala.BulkWriteResult
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.{and, elemMatch, equal}
import org.mongodb.scala.model.Updates

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

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

  def upsertRecipes(recipes: List[Recipe]): Future[BulkWriteResult] = {
    super.upsert(
      recipes.map((recipe) => {
        (recipe._id, getUpdates(recipe))
      })
    )
  }

 def loadInitialData(recipeDataPath: String, ingredientDataPath: String) = {

 }

  def processRecipeData(lines: Try[Iterator[String]]) : Map[String, Recipe] = lines match {
    case Success(lines) => {
      val recipes = for {
        line <- lines if !(line contains "Id")
      } yield Recipe(line.split(","))

      recipes.foldLeft(Map[String, Recipe]()) {
        (recipeMap, recipe) => recipeMap + (recipe._id -> recipe)
      }
    }
  }

  def processIngredientData(lines: Try[Iterator[String]], recipeMap: Map[String, Recipe]): Unit = lines match {
    case Success(lines) => {
     for {
       line <- lines if !(line contains "Id")
       (recipeId, ingredient) <- Ingredient(line.split(","))
     } yield recipeMap.get(recipeId).ingredients + ingredient
    }
  }

  private def buildFilter(field: String, value: String): Bson = {
    if (recipeFieldMap.contains(field)) {
      val (nestedDocuments, filterField) = recipeFieldMap(field)
      elemMatch(nestedDocuments, Document(filterField -> value))
    } else {
      equal(field, value)
    }
  }

  private def getUpdates(recipe: Recipe) = {
    Updates.combine(
      Updates.set("_id", recipe._id),
      Updates.set("title", recipe.title)
    )
  }
}
