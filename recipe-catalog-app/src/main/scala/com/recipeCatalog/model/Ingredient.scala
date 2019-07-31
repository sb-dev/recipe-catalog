package com.recipeCatalog.model

import com.recipeCatalog.common.model.Entity
import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}
import io.swagger.annotations.{ApiModel, ApiModelProperty}
import org.bson.codecs.configuration.CodecProvider
import org.mongodb.scala.bson.codecs.Macros
import io.circe.syntax._

import scala.annotation.meta.field

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
  def apply(lines: Array[String]): (String, Ingredient) = {
    case Array(recipeId: String, id: String, name: String, measure: String) => (recipeId, new Ingredient(id, name, measure))
  }

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
