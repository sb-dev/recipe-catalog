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

@ApiModel(description = "An author object")
case class Author(
 @(ApiModelProperty @field)(value = "Unique identifier for the author")
 override val _id: String,
 @(ApiModelProperty @field)(value = "Name of the author")
 name: String
) extends Entity(_id)

object Author {
  implicit val encoder: Encoder[Author] = new Encoder[Author] {
    override def apply(a: Author): Json = Json.obj(
      "id" -> a._id.asJson,
      "name" -> a.name.asJson
    )
  }

  implicit val decoder: Decoder[Author] = new Decoder[Author] {
    override def apply(c: HCursor): Result[Author] = for {
      id <- c.downField("id").as[Option[String]]
      name <- c.downField("name").as[Option[String]]
    } yield Author(id.getOrElse(""), name.getOrElse(""))
  }
  val authorCodecProvider: CodecProvider = Macros.createCodecProviderIgnoreNone[Author]()
}

@ApiModel(description = "An ingredient object")
case class Ingredient (
  @(ApiModelProperty @field)(value = "Unique identifier for the ingredient")
  override val _id: String,
  @(ApiModelProperty @field)(value = "Name of the ingredient")
  name: String,
  @(ApiModelProperty @field)(value = "Measure of quantity (format: value/unit)")
  measure: String,
) extends Entity(_id)

object Ingredient {
    implicit val encoder: Encoder[Ingredient] = new Encoder[Ingredient] {
      override def apply(a: Ingredient): Json = Json.obj(
        "id" -> a._id.asJson,
        "name" -> a.name.asJson,
        "measure" -> a.measure.asJson
      )
    }

    implicit val decoder: Decoder[Ingredient] = new Decoder[Ingredient] {
    override def apply(c: HCursor): Result[Ingredient] = for {
      id <- c.downField("id").as[Option[String]]
      name <- c.downField("name").as[Option[String]]
      measure <- c.downField("measure").as[Option[String]]
    } yield Ingredient(id.getOrElse(""), name.getOrElse(""), measure.getOrElse(""))
  }
  val ingredientCodecProvider: CodecProvider = Macros.createCodecProviderIgnoreNone[Ingredient]()
}
