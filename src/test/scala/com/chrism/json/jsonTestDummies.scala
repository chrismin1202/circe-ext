package com.chrism.json

import io.circe.{Encoder, Json}
import io.circe.generic.extras.decoding.ConfiguredDecoder
import io.circe.generic.extras.encoding.ConfiguredAsObjectEncoder
import shapeless.Lazy

private[json] final case class JsonTestDummy(
  name: String,
  booleanValue: Option[Boolean] = None,
  intValue: Option[Int] = None,
  doubleValue: Option[Double] = None
)(
  @transient
  private[this] implicit
  val encoder: Encoder[JsonTestDummy])
    extends CirceSerializable[JsonTestDummy] {

  override protected def encode: Json = encoder(this)
}

private[json] sealed trait JsonTestDummyCompanionLike {
  this: CirceJsonSerDeCompanionLike[JsonTestDummy] =>

  override protected lazy val encode: Lazy[ConfiguredAsObjectEncoder[JsonTestDummy]] = implicitly
  override protected lazy val decode: Lazy[ConfiguredDecoder[JsonTestDummy]] = implicitly
}

private[json] object CamelCasedJsonTestDummyCompanion
    extends CirceCamelCasedJsonSerDeCompanionLike[JsonTestDummy]
    with JsonTestDummyCompanionLike

private[json] object SnakeCasedJsonTestDummyCompanion
    extends CirceSnakeCasedJsonSerDeCompanionLike[JsonTestDummy]
    with JsonTestDummyCompanionLike

//private[json] object CamelCasedJsonTestDummyCompanion extends CirceCamelCasedJsonSerDeCompanionLike[JsonTestDummy] {
//
//  override protected lazy val encode: Lazy[ConfiguredAsObjectEncoder[JsonTestDummy]] = implicitly
//  override protected lazy val decode: Lazy[ConfiguredDecoder[JsonTestDummy]] = implicitly
//}
