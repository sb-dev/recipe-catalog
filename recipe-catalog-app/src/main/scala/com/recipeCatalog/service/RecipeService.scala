package com.recipeCatalog.service

import com.mongodb.client.result.DeleteResult
import com.recipeCatalog.model.Recipe
import com.recipeCatalog.repository.RecipeRepository

import scala.concurrent.{ExecutionContext, Future}

class RecipeService(recipeRepository: RecipeRepository)(implicit ec: ExecutionContext) {
  def findAll(queryParams: Map[String, String]): Future[Seq[Recipe]] = {
    if (queryParams.isEmpty)
      recipeRepository.queryAll()
    else
      recipeRepository.queryRecipes(queryParams)
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
