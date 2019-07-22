package com.recipeCatalog.model

import com.recipeCatalog.common.model.Entity
import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}
import io.swagger.annotations.{ApiModel, ApiModelProperty}
import org.bson.codecs.configuration.CodecProvider
import org.mongodb.scala.bson.codecs.Macros
import io.circe.syntax._

import scala.annotation.meta.field

@ApiModel(description = "An author object")
case class Author(
   @(ApiModelProperty @field)(value = "Unique identifier for the author")
   override val _id: String,
   @(ApiModelProperty @field)(value = "Name of the author")
   name: String
) extends Entity(_id)

object Author {
  def apply(line: Array[String]): Author = line match {
    case Array(id: String, name: String) => new Author(id, name)
  }

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
