package com.recipeCatalog.service

import com.mongodb.client.result.DeleteResult
import com.recipeCatalog.model.{Author, Recipe}
import com.recipeCatalog.repository.{AuthorRepository, RecipeRepository}

import scala.concurrent.Future

class AuthorService(authorRepository: AuthorRepository, recipeRepository: RecipeRepository) {
  def findAll(): Future[Seq[Author]] = {
    authorRepository.queryAll()
  }

  def findOne(id: String): Future[Option[Author]] = {
    authorRepository.query(id)
  }

  def findRecipes(authorId: String): Future[Seq[Recipe]] = {
    recipeRepository.findByAuthor(authorId)
  }

  def update(id: String, author: Author): Future[Author] = {
    authorRepository.update(id, author)
  }

  def save(author: Author): Future[String]  = {
    authorRepository.insert(author)
  }

  def saveRecipe(authorId: String, recipe: Recipe): Future[String]  = {
    recipeRepository.insert(Recipe(recipe._id, recipe.title, authorId, recipe.publishDate))
  }

  def delete(id: String): Future[DeleteResult] = {
    recipeRepository.delete(id)
  }
}
