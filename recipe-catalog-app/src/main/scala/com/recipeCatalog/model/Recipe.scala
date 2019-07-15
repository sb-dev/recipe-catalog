package com.recipeCatalog.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.recipeCatalog.common.model.Entity
import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}
import io.swagger.annotations.{ApiModel, ApiModelProperty}
import org.bson.codecs.configuration.CodecProvider
import org.mongodb.scala.bson.codecs.Macros
import io.circe.syntax._

import scala.annotation.meta.field

@ApiModel(description = "An recipe object")
case class Recipe (
  @(ApiModelProperty @field)(value = "Unique identifier for the recipe")
  override val _id: String,
  @(ApiModelProperty @field)(value = "Title of the recipe")
  title: String,
  @(ApiModelProperty @field)(value = "Unique identifier for the author")
  authorId: String,
  @(ApiModelProperty @field)(value = "Date of publication")
  publishDate: LocalDate,
  @(ApiModelProperty @field)(value = "Ingredients required for the recipe")
  ingredients: List[Ingredient]) extends Entity(_id)

object Recipe {
  private val formatter = DateTimeFormatter.ISO_DATE

  implicit val encoder: Encoder[Recipe] = new Encoder[Recipe] {
    override def apply(a: Recipe): Json = Json.obj(
      "id" -> a._id.asJson,
      "title" -> a.title.asJson,
      "authorId" -> a.authorId.asJson,
      "ingredients" -> a.ingredients.asJson,
      "publishDate" -> formatter.format(a.publishDate).asJson
    )
  }

  implicit val decoder: Decoder[Recipe] = new Decoder[Recipe] {
    override def apply(c: HCursor): Result[Recipe] = for {
      id <- c.downField("id").as[Option[String]]
      title <- c.downField("title").as[Option[String]]
      publishDate <- c.downField("publishDate").as[Option[String]]
      authorId <- c.downField("authorId").as[Option[String]]
      ingredients <- c.downField("ingredients").as[Option[List[Ingredient]]]
    } yield Recipe(id.getOrElse(""), title.getOrElse(""), authorId.getOrElse(""), LocalDate.parse(publishDate.get, formatter), ingredients.getOrElse(List()))
  }

  val recipeCodecProvider: CodecProvider = Macros.createCodecProviderIgnoreNone[Recipe]()
}
