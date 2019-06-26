package com.recipeCatalog.model

import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}
import org.bson.codecs.configuration.CodecProvider
import org.bson.types.ObjectId
import org.joda.time.LocalDate
import org.joda.time.format.ISODateTimeFormat
import org.mongodb.scala.bson.codecs.Macros

case class Recipe(_id: ObjectId, title: String, authorId: String, publishDate: LocalDate)

object Recipe {
  private val formatter = ISODateTimeFormat.date()

  implicit val encoder: Encoder[Recipe] = new Encoder[Recipe] {
    override def apply(a: Recipe): Json = Json.obj(
      "id" -> Json.fromString(a._id.toHexString),
      "title" -> Json.fromString(a.title),
      "authorId" -> Json.fromString(a.authorId),
      "publishDate" -> Json.fromString(a.publishDate.toString(formatter))
    )
  }

  implicit val decoder: Decoder[Recipe] = new Decoder[Recipe] {
    override def apply(c: HCursor): Result[Recipe] = for {
      title <- c.get[String]("title")
      publishDate <- c.get[String]("publishDate")
      authorId <- c.get[String]("authorId")
    } yield Recipe(ObjectId.get(), title, authorId, formatter.parseLocalDate(publishDate))
  }

  val recipeCodecProvider: CodecProvider = Macros.createCodecProviderIgnoreNone[Recipe]()
}

case class Author(_id: ObjectId, name: String)

object Author {
  implicit val encoder: Encoder[Author] = new Encoder[Author] {
    override def apply(a: Author): Json = Json.obj(
      "id" -> Json.fromString(a._id.toHexString)
    )
  }

  implicit val decoder: Decoder[Author] = new Decoder[Author] {
    override def apply(c: HCursor): Result[Author] = for {
      name <- c.get[String]("name")
    } yield Author(ObjectId.get(), name)
  }
  val authorCodecProvider: CodecProvider = Macros.createCodecProviderIgnoreNone[Author]()
}
