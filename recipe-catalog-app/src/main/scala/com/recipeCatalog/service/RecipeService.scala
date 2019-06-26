package com.recipeCatalog.service

import com.recipeCatalog.model.Recipe
import com.recipeCatalog.repository.RecipeRepository

import scala.concurrent.{ExecutionContext, Future}

class RecipeService(recipeRepository: RecipeRepository)(implicit ec: ExecutionContext) {
  def findAll(): Future[Seq[Recipe]] = {
    recipeRepository.queryAll()
  }

  def findOne(id: String): Future[Option[Recipe]] = {
    recipeRepository.query(id)
  }
}
