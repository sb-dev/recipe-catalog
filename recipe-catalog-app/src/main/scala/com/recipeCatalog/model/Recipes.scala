package com.recipeCatalog.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.recipeCatalog.common.model.Entity
import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}
import io.swagger.annotations.{ApiModel, ApiModelProperty}
import org.bson.codecs.configuration.CodecProvider
import org.mongodb.scala.bson.codecs.Macros

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
  publishDate: LocalDate) extends Entity(_id)

object Recipe {
  private val formatter = DateTimeFormatter.ISO_DATE

  implicit val encoder: Encoder[Recipe] = new Encoder[Recipe] {
    override def apply(a: Recipe): Json = Json.obj(
      "id" -> Json.fromString(a._id),
      "title" -> Json.fromString(a.title),
      "authorId" -> Json.fromString(a.authorId),
      "publishDate" -> Json.fromString(formatter.format(a.publishDate))
    )
  }

  implicit val decoder: Decoder[Recipe] = new Decoder[Recipe] {
    override def apply(c: HCursor): Result[Recipe] = for {
      id <- c.downField("id").as[Option[String]]
      title <- c.downField("title").as[Option[String]]
      publishDate <- c.downField("publishDate").as[Option[String]]
      authorId <- c.downField("authorId").as[Option[String]]
    } yield Recipe(id.getOrElse(""), title.getOrElse(""), authorId.getOrElse(""), LocalDate.parse(publishDate.get, formatter))
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
      "id" -> Json.fromString(a._id),
      "name" -> Json.fromString(a.name)
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
