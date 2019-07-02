package com.recipeCatalog.swagger

import com.github.swagger.akka.SwaggerHttpService
import com.github.swagger.akka.model.Info
import com.recipeCatalog.routes.{AuthorRoute, RecipeRoute}

object SwaggerDocService extends SwaggerHttpService {

  override val apiClasses = Set(classOf[AuthorRoute], classOf[RecipeRoute])
  override val host = "localhost:8080"
  override val info = Info(version = "1.0")
}