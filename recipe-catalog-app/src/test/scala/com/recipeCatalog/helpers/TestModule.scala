package com.recipeCatalog.helpers

import com.recipeCatalog.Module
import com.recipeCatalog.common.repository.{IdGenerator, Mongo}

trait TestModule extends Module {
  override lazy val mongo: Mongo = TestMongo(codecRegistry)
  override lazy val idGenerator: IdGenerator = new IdGenerator { override def generate: String = "random_string" }
}

object TestModule extends TestModule
