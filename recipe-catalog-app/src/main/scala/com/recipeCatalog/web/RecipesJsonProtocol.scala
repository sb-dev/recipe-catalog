package com.recipeCatalog.web

import com.recipeCatalog.model.{Author, Recipe}
import org.joda.time.LocalDate

case class RecipeResource(id: String, title: String, publishingDate: LocalDate, author: Author)
case class AuthorResource(id: Option[Long], name: String)

object RecipeResource {
  def apply(recipe: Recipe, author: Author): RecipeResource =
    RecipeResource(recipe._id.toHexString, recipe.title, recipe.publishDate, author)
}

//object RecipesJsonProtocol extends DefaultJsonProtocol {
//  implicit object LocalDateJsonProtocol extends JsonFormat[LocalDate] {
//    private val formatter = ISODateTimeFormat.date()
//
//    def write(date: LocalDate) = JsString(date.toString(formatter))
//
//    def read(value: JsValue) = value match{
//      case JsString(date) => formatter.parseLocalDate(date)
//      case _ => deserializationError("String value expected")
//    }
//  }
//
//  implicit def AuthorFormat = jsonFormat2(Author.apply)
//  implicit def recipeResourceFormat = jsonFormat4(RecipeResource.apply)
//}