package com.recipeCatalog.service

import com.mongodb.client.result.DeleteResult
import com.recipeCatalog.model.Recipe
import com.recipeCatalog.repository.RecipeRepository

import scala.concurrent.{ExecutionContext, Future}

class RecipeService(recipeRepository: RecipeRepository)(implicit ec: ExecutionContext) {
  def findAll(queryParamsMap: Map[String, String]): Future[Seq[Recipe]] = {
    if (queryParamsMap.isEmpty)
      recipeRepository.queryAll()
    else {
      var queryParams: Set[(String,String)] = Set()

      for {
        key <- queryParamsMap.keySet
        values <- queryParamsMap.values
      } yield values.split(",").map(
        value => queryParams += ((key, value))
      )

      recipeRepository.queryRecipes(queryParams)
    }
  }

  def findOne(id: String): Future[Option[Recipe]] = {
    recipeRepository.query(id)
  }

  def update(id: String, recipe: Recipe): Future[Option[Recipe]] = {
    recipeRepository.update(id, recipe)
  }

  def save(recipe: Recipe): Future[String]  = {
    recipeRepository.insert(recipe)
  }

  def delete(id: String): Future[DeleteResult] = {
    recipeRepository.delete(id)
  }
}
