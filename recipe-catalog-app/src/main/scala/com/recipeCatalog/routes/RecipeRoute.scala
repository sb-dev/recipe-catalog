package com.recipeCatalog.routes

import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import com.recipeCatalog.service.RecipeService
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._

import scala.concurrent.ExecutionContext

/**
  * GET    /api/v1/recipes
  * GET    /api/v1/recipes/{id}
  * PUT    /api/v1/recipes/{id}
  * Delete /api/v1/recipes/{id}
  *
  * GET    /api/v1/authors
  * POST   /api/v1/authors
  * GET    /api/v1/authors/{id}
  * PUT    /api/v1/authors/{id}
  * DELETE /api/v1/authors/{id}
  *
  * GET    /api/v1/authors/{id}/recipes
  * POST   /api/v1/authors/{id}/recipes
  */

class RecipeRoute(recipeService: RecipeService)(implicit ec: ExecutionContext, mat: Materializer) {
 val recipeRoutes =
   pathPrefix("api" / "recipes") {
     get {
       complete {
         recipeService.findAll()
       }
     }
   }
}
