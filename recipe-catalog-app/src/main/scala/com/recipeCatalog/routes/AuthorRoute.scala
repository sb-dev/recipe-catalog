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

class AuthorRoute(authorService: AuthorService)(implicit ec: ExecutionContext, mat: Materializer) {
  private lazy val findAll: Route = (get & pathEndOrSingleSlash) {
    onComplete(authorService.findAll()) {
      case Success(authors) =>
        complete(Marshal(authors).to[ResponseEntity].map{e => HttpResponse(entity = e)})
      case Failure(e) =>
        complete(handleFailure(e.getMessage))
    }
  }

  private lazy val save: Route = (post & pathEndOrSingleSlash & entity(as[Author])) { author =>
    onComplete(authorService.save(author)) {
      case Success(id) =>
        complete(handleCreated("author", id))
      case Failure(e) =>
        complete(handleFailure(e.getMessage))
    }
  }

  private lazy val findById: Route = (get & pathPrefix(Segment).as(FindByIdRequest) & pathEndOrSingleSlash) { request =>
    onComplete(authorService.findOne(request.id)) {
      case Success(Some(author)) =>
        complete(Marshal(author).to[ResponseEntity].map { e => HttpResponse(entity = e) })
      case Success(None) =>
        complete(handleNotFound)
      case Failure(e) =>
        complete(handleFailure(e.getMessage))
    }
  }

  private lazy val update: Route = (put & pathPrefix(Segment).as(FindByIdRequest) & entity(as[Author])) { (request, author) =>
    onComplete(authorService.update(request.id, author)) {
      case Success(Some(author)) =>
        complete(handleCreated("author", author._id))
      case Success(None) =>
        complete(handleNotFound)
      case Failure(e) =>
        complete(handleFailure(e.getMessage))
    }
  }

  private lazy val remove: Route = (delete & pathPrefix(Segment).as(FindByIdRequest) & pathEndOrSingleSlash) { request =>
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

  private lazy val findRecipes: Route = (get & pathPrefix(Segment).as(FindByIdRequest) & pathPrefix("recipes")) { request =>
    onComplete(authorService.findRecipes(request.id)) {
      case Success(recipes) =>
        complete(Marshal(recipes).to[ResponseEntity].map { e => HttpResponse(entity = e) })
      case Failure(e) =>
        complete(handleFailure(e.getMessage))
    }
  }

  private lazy val createRecipe: Route = (post & pathPrefix(Segment).as(FindByIdRequest) & pathPrefix("recipes") & entity(as[Recipe])) { (request, recipe) =>
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
