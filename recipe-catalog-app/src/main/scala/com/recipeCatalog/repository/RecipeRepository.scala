package com.recipeCatalog.repository

import com.recipeCatalog.common.repository.{Mongo, MongoRepository}
import com.recipeCatalog.model.Recipe

import scala.concurrent.ExecutionContext

class RecipeRepository(val mongo: Mongo)(implicit ec: ExecutionContext)  extends MongoRepository[Recipe, String] {
  override def collectionName: String = "recipes"
}
