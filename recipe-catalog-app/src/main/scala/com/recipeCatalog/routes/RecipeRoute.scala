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
import io.swagger.annotations.{Api, ApiImplicitParam, ApiImplicitParams, ApiOperation}
import javax.ws.rs.{DELETE, GET, PUT, Path}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
  * GET    /api/recipes
  * GET    /api/recipes/{id}
  * PUT    /api/recipes/{id}
  * Delete /api/recipes/{id}
  */

@Path("/api/recipes")
@Api(value = "/api/recipes", produces = "application/json")
class RecipeRoute(recipeService: RecipeService)(implicit ec: ExecutionContext, mat: Materializer) {
  @GET
  @Path("/")
  @ApiOperation(
    httpMethod = "GET",
    response = classOf[Recipe],
    responseContainer = "List",
    value = "Returns a list of recipes"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(name="ingredientIds", value = "ingredients IDs to filter recipes on", required = false, dataType = "String", paramType = "query")
    )
  )
 def findAll: Route = (get & pathEndOrSingleSlash & parameterMap) { parameterMap =>
   onComplete(recipeService.findAll(parameterMap)) {
     case Success(recipes) =>
       complete(Marshal(recipes).to[ResponseEntity].map{e => HttpResponse(entity = e)})
     case Failure(e) =>
       complete(handleFailure(e.getMessage))
   }
 }

  @GET
  @Path("/{recipeId}")
  @ApiOperation(
    httpMethod = "GET",
    response = classOf[Recipe],
    value = "Returns an recipe based on ID"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(name="recipeId", value = "recipe ID", required = true, dataType = "String", paramType = "path")
    )
  )
 def findById: Route = (get & path(Segment).as(FindByIdRequest)) { request =>
   onComplete(recipeService.findOne(request.id)) {
     case Success(Some(recipe)) =>
       complete(Marshal(recipe).to[ResponseEntity].map{e => HttpResponse(entity = e)})
     case Success(None) =>
       complete(handleNotFound)
     case Failure(e) =>
       complete(handleFailure(e.getMessage))
   }
 }

  @PUT
  @Path("/")
  @ApiOperation(
    httpMethod = "PUT",
    response = classOf[Void],
    value = "Update recipe based on recipe ID"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(value = "recipe", required = true, dataTypeClass = classOf[Recipe], paramType = "body")
    )
  )
 def update: Route = (put & path(Segment).as(FindByIdRequest) & entity(as[Recipe])) { (request, recipe) =>
   onComplete(recipeService.update(request.id, recipe)) {
     case Success(Some(recipe)) =>
       complete(handleCreated("recipe", recipe._id))
     case Success(None) =>
       complete(handleNotFound)
     case Failure(e) =>
       complete(handleFailure(e.getMessage))
   }
 }

  @DELETE
  @Path("/{recipeId}")
  @ApiOperation(
    httpMethod = "DELETE",
    response = classOf[Void],
    value = "Deletes a recipe based on ID"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(value = "recipe ID", required = true, dataType = "String", paramType = "path")
    )
  )
 def remove: Route = (delete & path(Segment).as(FindByIdRequest)) { request =>
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
