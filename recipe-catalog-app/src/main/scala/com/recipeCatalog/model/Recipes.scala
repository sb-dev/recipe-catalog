package com.recipeCatalog.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.recipeCatalog.common.model.Entity
import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}
import org.bson.codecs.configuration.CodecProvider
import org.bson.types.ObjectId
import org.mongodb.scala.bson.codecs.Macros

case class Recipe (override val _id: ObjectId, title: String, authorId: String, publishDate: LocalDate) extends Entity(_id)

object Recipe {
  private val formatter = DateTimeFormatter.ISO_DATE

  implicit val encoder: Encoder[Recipe] = new Encoder[Recipe] {
    override def apply(a: Recipe): Json = Json.obj(
      "id" -> Json.fromString(a._id.toHexString),
      "title" -> Json.fromString(a.title),
      "authorId" -> Json.fromString(a.authorId),
      "publishDate" -> Json.fromString(formatter.format(a.publishDate))
    )
  }

  implicit val decoder: Decoder[Recipe] = new Decoder[Recipe] {
    override def apply(c: HCursor): Result[Recipe] = for {
      title <- c.get[String]("title")
      publishDate <- c.get[String]("publishDate")
      authorId <- c.get[String]("authorId")
    } yield Recipe(ObjectId.get(), title, authorId, LocalDate.parse(publishDate, formatter))
  }

  val recipeCodecProvider: CodecProvider = Macros.createCodecProviderIgnoreNone[Recipe]()
}

case class Author(override val _id: ObjectId, name: String) extends Entity(_id)

object Author {
  implicit val encoder: Encoder[Author] = new Encoder[Author] {
    override def apply(a: Author): Json = Json.obj(
      "id" -> Json.fromString(a._id.toHexString),
      "name" -> Json.fromString(a.name)
    )
  }

  implicit val decoder: Decoder[Author] = new Decoder[Author] {
    override def apply(c: HCursor): Result[Author] = for {
      name <- c.get[String]("name")
    } yield Author(ObjectId.get(), name)
  }
  val authorCodecProvider: CodecProvider = Macros.createCodecProviderIgnoreNone[Author]()
}
