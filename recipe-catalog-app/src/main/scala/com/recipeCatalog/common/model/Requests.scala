package com.recipeCatalog.common.model

import io.circe.{Encoder, Json}
import org.bson.types.ObjectId

case class FindByIdRequest(id: String) {
  require(ObjectId.isValid(id), "the informed id is not a representation of a valid hex string")
}

case class Message(message: String)

object Message {
  implicit val encoder: Encoder[Message] = m => Json.obj("message" -> Json.fromString(m.message))
}
