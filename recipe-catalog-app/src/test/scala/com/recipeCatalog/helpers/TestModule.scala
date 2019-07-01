package com.recipeCatalog.helpers

import com.recipeCatalog.Module
import com.recipeCatalog.common.repository.Mongo

trait TestModule extends Module {
  override lazy val mongo: Mongo = TestMongo(codecRegistry)
}

object TestModule extends TestModule
