package com.recipeCatalog.routes

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{HttpResponse, ResponseEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import com.recipeCatalog.common.http.Helpers._
import com.recipeCatalog.common.model.FindByIdRequest
import com.recipeCatalog.model.Recipe
import com.recipeCatalog.service.RecipeService
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
  * GET    /api/recipes
  * GET    /api/recipes/{id}
  * PUT    /api/recipes/{id}
  * Delete /api/recipes/{id}
  */

class RecipeRoute(recipeService: RecipeService)(implicit ec: ExecutionContext, mat: Materializer) {
 private lazy val findAll: Route = (get & pathEndOrSingleSlash) {
   onComplete(recipeService.findAll()) {
     case Success(recipes) =>
       complete(Marshal(recipes).to[ResponseEntity].map{e => HttpResponse(entity = e)})
     case Failure(e) =>
       complete(handleFailure(e.getMessage))
   }
 }

 private lazy val findById: Route = (get & path(Segment).as(FindByIdRequest)) { request =>
   onComplete(recipeService.findOne(request.id)) {
     case Success(Some(recipe)) =>
       complete(Marshal(recipe).to[ResponseEntity].map{e => HttpResponse(entity = e)})
     case Success(None) =>
       complete(handleNotFound)
     case Failure(e) =>
       complete(handleFailure(e.getMessage))
   }
 }

 private lazy val update: Route = (put & path(Segment).as(FindByIdRequest) & entity(as[Recipe])) { (request, recipe) =>
   onComplete(recipeService.update(request.id, recipe)) {
     case Success(Some(recipe)) =>
       complete(handleCreated("recipe", recipe._id))
     case Success(None) =>
       complete(handleNotFound)
     case Failure(e) =>
       complete(handleFailure(e.getMessage))
   }
 }

 private lazy val remove: Route = (delete & path(Segment).as(FindByIdRequest)) { request =>
   onComplete(recipeService.delete(request.id)) {
     case Success(result) =>
       if (result.wasAcknowledged())
         complete(handleSuccess)
       else
         complete(handleFailure("Failed to delete"))
     case Failure(e) =>
       complete(handleFailure(e.getMessage))
   }
 }

 val routes: Route =
   pathPrefix("api" / "recipes") {
      findAll ~ findById ~ update ~ remove
   }
}
