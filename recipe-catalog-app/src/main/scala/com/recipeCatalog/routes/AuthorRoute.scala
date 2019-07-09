package com.recipeCatalog.routes

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{HttpResponse, ResponseEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import com.recipeCatalog.common.http.Helpers._
import com.recipeCatalog.common.model.FindByIdRequest
import com.recipeCatalog.model.{Author, Recipe}
import com.recipeCatalog.service.AuthorService
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import io.swagger.annotations._
import javax.ws.rs.{DELETE, GET, POST, PUT, Path}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
  * GET    /api/authors
  * POST   /api/authors
  * GET    /api/authors/{id}
  * PUT    /api/authors/{id}
  * DELETE /api/authors/{id}
  *
  * GET    /api/authors/{id}/recipes
  * POST   /api/authors/{id}/recipes
  */

@Path("/api/authors")
@Api(value = "/api/authors", produces = "application/json")
class AuthorRoute(authorService: AuthorService)(implicit ec: ExecutionContext, mat: Materializer) {

  @GET
  @Path("/")
  @ApiOperation(
    httpMethod = "GET",
    response = classOf[Author],
    responseContainer = "List",
    value = "Returns a list of authors"
  )
  def findAll: Route = (get & pathEndOrSingleSlash) {
    onComplete(authorService.findAll()) {
      case Success(authors) =>
        complete(Marshal(authors).to[ResponseEntity].map{e => HttpResponse(entity = e)})
      case Failure(e) =>
        complete(handleFailure(e.getMessage))
    }
  }

  @POST
  @Path("/")
  @ApiOperation(
    httpMethod = "POST",
    response = classOf[Void],
    value = "Creates author"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(value = "author", required = true, dataTypeClass = classOf[Author], paramType = "body")
    )
  )
  def save: Route = (post & pathEndOrSingleSlash & entity(as[Author])) { author =>
    onComplete(authorService.save(author)) {
      case Success(id) =>
        complete(handleCreated("author", id))
      case Failure(e) =>
        complete(handleFailure(e.getMessage))
    }
  }

  @GET
  @Path("/{authorId}")
  @ApiOperation(
    httpMethod = "GET",
    response = classOf[Author],
    value = "Returns an author based on ID"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(name="authorId", value = "author ID", required = true, dataType = "String", paramType = "path")
    )
  )
  def findById: Route = (get & pathPrefix(Segment).as(FindByIdRequest) & pathEndOrSingleSlash) { request =>
    onComplete(authorService.findOne(request.id)) {
      case Success(Some(author)) =>
        complete(Marshal(author).to[ResponseEntity].map { e => HttpResponse(entity = e) })
      case Success(None) =>
        complete(handleNotFound)
      case Failure(e) =>
        complete(handleFailure(e.getMessage))
    }
  }

  @PUT
  @Path("/{authorId}")
  @ApiOperation(
    httpMethod = "PUT",
    response = classOf[Void],
    value = "Update author based on author ID"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(value = "author", required = true, dataTypeClass = classOf[Author], paramType = "body")
    )
  )
  def update: Route = (put & pathPrefix(Segment).as(FindByIdRequest) & entity(as[Author])) { (request, author) =>
    onComplete(authorService.update(request.id, author)) {
      case Success(Some(author)) =>
        complete(handleCreated("author", author._id))
      case Success(None) =>
        complete(handleNotFound)
      case Failure(e) =>
        complete(handleFailure(e.getMessage))
    }
  }

  @DELETE
  @Path("/{authorId}")
  @ApiOperation(
    httpMethod = "DELETE",
    response = classOf[Void],
    value = "Deletes an author based on ID"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(value = "author ID", required = true, dataType = "String", paramType = "path")
    )
  )
  def remove: Route = (delete & pathPrefix(Segment).as(FindByIdRequest) & pathEndOrSingleSlash) { request =>
    onComplete(authorService.delete(request.id)) {
      case Success(result) =>
        if (result.wasAcknowledged())
          complete(handleSuccess)
        else
          complete(handleFailure("Failed to delete"))
      case Failure(e) =>
        complete(handleFailure(e.getMessage))
    }
  }

  @GET
  @Path("/{authorId}/recipes")
  @ApiOperation(
    httpMethod = "GET",
    response = classOf[Author],
    value = "Returns recipes based on author ID"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(value = "author ID", required = true, dataType = "String", paramType = "path")
    )
  )
  def findRecipes: Route = (get & pathPrefix(Segment).as(FindByIdRequest) & pathPrefix("recipes")) { request =>
    onComplete(authorService.findRecipes(request.id)) {
      case Success(recipes) =>
        complete(Marshal(recipes).to[ResponseEntity].map { e => HttpResponse(entity = e) })
      case Failure(e) =>
        complete(handleFailure(e.getMessage))
    }
  }

  @POST
  @Path("/{authorId}/recipes")
  @ApiOperation(
    httpMethod = "POST",
    response = classOf[Void],
    value = "Creates recipe based on author ID"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(value = "recipe", required = true, dataTypeClass = classOf[Recipe], paramType = "body")
    )
  )
  def createRecipe: Route = (post & pathPrefix(Segment).as(FindByIdRequest) & pathPrefix("recipes") & entity(as[Recipe])) { (request, recipe) =>
    onComplete(authorService.saveRecipe(request.id, recipe)) {
      case Success(id) =>
        complete(handleCreated("recipe", id))
      case Failure(e) =>
        complete(handleFailure(e.getMessage))
    }
  }

  val routes: Route = pathPrefix("api" / "authors") {
    findAll ~ save ~ findById ~ update ~ remove ~ findRecipes ~ createRecipe
  }
}
