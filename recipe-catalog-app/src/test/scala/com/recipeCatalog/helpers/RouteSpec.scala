package com.recipeCatalog.helpers

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

abstract class RouteSpec extends WordSpec with Matchers with ScalatestRouteTest {

  implicit lazy val module = TestModule

}
