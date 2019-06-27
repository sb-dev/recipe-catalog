package com.recipeCatalog.routes

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.model.{HttpResponse, ResponseEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import com.recipeCatalog.common.model.{FindByIdRequest, Message}
import com.recipeCatalog.model.Recipe
import com.recipeCatalog.service.RecipeService
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

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
       onComplete(recipeService.findAll()) {
         case Success(recipes) =>
          complete(Marshal(recipes).to[ResponseEntity].map{e => HttpResponse(entity = e)})
         case Failure(e) =>
           complete(Marshal(Message(e.getMessage)).to[ResponseEntity].map{e => HttpResponse(entity = e, status = StatusCodes.InternalServerError)})
       }
     } ~ (get & path(Segment).as(FindByIdRequest)) { request =>
       onComplete(recipeService.findOne(request.id)) {
         case Success(Some(recipe)) =>
           complete(Marshal(recipe).to[ResponseEntity].map{e => HttpResponse(entity = e)})
         case Success(None) =>
           complete(HttpResponse(status = StatusCodes.NotFound))
         case Failure(e) =>
           complete(Marshal(Message(e.getMessage)).to[ResponseEntity].map{e => HttpResponse(entity = e)})
       }
     } ~ (post & pathEndOrSingleSlash & entity(as[Recipe])) { recipe =>
       onComplete(recipeService.save(recipe)) {
         case Success(id) =>
           complete(HttpResponse(status = StatusCodes.Created, headers = List(Location(s"api/recipe/$id"))))
         case Failure(e) =>
           complete(Marshal(Message(e.getMessage)).to[ResponseEntity].map{e => HttpResponse(entity = e)})
       }
     }
   }
}
