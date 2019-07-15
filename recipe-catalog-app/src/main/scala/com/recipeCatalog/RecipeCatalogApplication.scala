package com.recipeCatalog

import akka.http.scaladsl.server.Directives._

object RecipeCatalogApplication extends Module {
  startApplication(recipeRoutes.routes ~ authorRoutes.routes ~ swaggerRoutes)
}
