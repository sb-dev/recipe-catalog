package com.recipeCatalog

import akka.http.scaladsl.server.Directives._
import com.recipeCatalog.common.repository.DataLoader

object RecipeCatalogApplication extends Module {

  val authorData = DataLoader.loadData("/data/authors.csv")
  authorRepository.processData(authorData)

  startApplication(recipeRoutes.routes ~ authorRoutes.routes ~ swaggerRoutes)
}
