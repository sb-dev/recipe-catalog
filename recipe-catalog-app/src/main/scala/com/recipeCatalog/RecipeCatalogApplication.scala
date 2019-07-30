package com.recipeCatalog

import akka.http.scaladsl.server.Directives._
import com.recipeCatalog.common.repository.DataLoader

object RecipeCatalogApplication extends Module {

  authorRepository.loadInitialData("/data/authors.csv")

  startApplication(recipeRoutes.routes ~ authorRoutes.routes ~ swaggerRoutes)
}
