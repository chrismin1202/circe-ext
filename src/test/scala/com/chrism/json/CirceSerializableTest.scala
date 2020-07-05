package com.chrism.json

import com.chrism.commons.FunTestSuite
import io.circe.Json

import scala.io.Source

final class CirceSerializableTest extends FunTestSuite {

  import CirceSerializableTest.readResourceFile

  test("serializing/deserializing camelCased JSON") {
    import CamelCasedJsonTestDummyCompanion._

    val json = readResourceFile("camelCasedJson.json")
    val obj = CamelCasedJsonTestDummyCompanion.fromJson(json)

    val expectedObj = JsonTestDummy("camelCased", booleanValue = Some(false), intValue = Some(665))
    assert(obj === expectedObj)

    val expectedJson = Json.obj(
      "name" -> Json.fromString("camelCased"),
      "booleanValue" -> Json.False,
      "intValue" -> Json.fromInt(665),
    )
    assert(obj.toJson === expectedJson)
  }

  test("serializing/deserializing snake_cased JSON") {
    import SnakeCasedJsonTestDummyCompanion._

    val json = readResourceFile("snake_cased_json.json")
    val obj = SnakeCasedJsonTestDummyCompanion.fromJson(json)

    val expectedObj =
      JsonTestDummy("snake_cased", booleanValue = Some(true), intValue = Some(666), doubleValue = Some(2.0))
    assert(obj === expectedObj)

    val expectedJson = Json.obj(
      "name" -> Json.fromString("snake_cased"),
      "boolean_value" -> Json.True,
      "int_value" -> Json.fromInt(666),
      "double_value" -> Json.fromDoubleOrNull(2.0),
    )
    assert(obj.toJson === expectedJson)
  }
}

private[this] object CirceSerializableTest {

  private[this] val BasePath: String = "/com/chrism/json"

  private def readResourceFile(name: String): String =
    Source.fromInputStream(getClass.getResourceAsStream(s"$BasePath/$name")).mkString
}
