package com.recipeCatalog.common.repository

import java.util.UUID

trait IdGenerator {
  def generate: String = UUID.randomUUID().toString
}

object IdGenerator extends IdGenerator