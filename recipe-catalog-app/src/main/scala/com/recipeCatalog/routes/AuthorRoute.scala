package com.recipeCatalog.routes

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{HttpResponse, ResponseEntity}
import akka.http.scaladsl.server.Directives._
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
  val authorRoutes =
    pathPrefix("api" / "authors") {
      (get & pathEndOrSingleSlash) {
        onComplete(authorService.findAll()) {
          case Success(authors) =>
            complete(Marshal(authors).to[ResponseEntity].map{e => HttpResponse(entity = e)})
          case Failure(e) =>
            complete(handleFailure(e.getMessage))
        }
      } ~ (post & pathEndOrSingleSlash & entity(as[Author])) { author =>
        onComplete(authorService.save(author)) {
          case Success(id) =>
            complete(handleCreated("author", id))
          case Failure(e) =>
            complete(handleFailure(e.getMessage))
        }
      } ~ (pathPrefix(Segment).as(FindByIdRequest)) { request =>
        (get & pathEndOrSingleSlash) {
          onComplete(authorService.findOne(request.id)) {
            case Success(Some(author)) =>
              complete(Marshal(author).to[ResponseEntity].map{e => HttpResponse(entity = e)})
            case Success(None) =>
              complete(handleNotFound)
            case Failure(e) =>
              complete(handleFailure(e.getMessage))
          }
        } ~ (put & entity(as[Author])) { author =>
          onComplete(authorService.update(request.id, author)) {
            case Success(Some(author)) =>
              complete(handleCreated("author", author._id.toHexString))
            case Success(None) =>
              complete(handleNotFound)
            case Failure(e) =>
              complete(handleFailure(e.getMessage))
          }
        } ~ delete {
          onComplete(authorService.delete(request.id)) {
            case Success(result) =>
              if(result.wasAcknowledged())
                complete(handleSuccess)
              else
                complete(handleFailure("Failed to delete"))
            case Failure(e) =>
              complete(handleFailure(e.getMessage))
          }
        } ~ (pathPrefix("recipes") & pathEndOrSingleSlash) {
          get {
            onComplete(authorService.findRecipes(request.id)) {
              case Success(recipes) =>
                complete(Marshal(recipes).to[ResponseEntity].map{e => HttpResponse(entity = e)})
              case Failure(e) =>
                complete(handleFailure(e.getMessage))
            }
          } ~ (post & entity(as[Recipe])) { recipe =>
            onComplete(authorService.saveRecipe(request.id, recipe)) {
              case Success(id) =>
                complete(handleCreated("recipe", id))
              case Failure(e) =>
                complete(handleFailure(e.getMessage))
            }
          }
        }
      }
    }
}
