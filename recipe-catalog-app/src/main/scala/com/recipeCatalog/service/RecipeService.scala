package com.recipeCatalog.service

import com.recipeCatalog.common.repository.Mongo
import com.recipeCatalog.model.Recipe
import com.recipeCatalog.repository.{AuthorRepository, RecipeRepository}

import scala.concurrent.{ExecutionContext, Future}

class RecipeService(val repository: Mongo)(implicit ec: ExecutionContext) {
  def recipeRepository: RecipeRepository = new RecipeRepository(repository)
  def authorRepository: AuthorRepository = new AuthorRepository(repository)

  def findAll(): Future[Seq[Recipe]] = {
    recipeRepository.queryAll()
  }
}
