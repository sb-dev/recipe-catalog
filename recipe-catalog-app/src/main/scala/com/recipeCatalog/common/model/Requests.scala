package com.recipeCatalog.common.model

import io.circe.{Encoder, Json}

case class FindByIdRequest(id: String) {}

case class Message(message: String)

object Message {
  implicit val encoder: Encoder[Message] = m => Json.obj("message" -> Json.fromString(m.message))
}
