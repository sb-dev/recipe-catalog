package com.recipeCatalog.common.repository

import scala.io.Source
import scala.util.Try

object DataLoader {
  def loadData(resourceName: String): Try[Iterator[String]] = {
    for {
      fileStream <- Try(getClass.getResourceAsStream(resourceName))
      source <- Try(Source.fromInputStream(fileStream))
    } yield source.getLines()
  }
}
