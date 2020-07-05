/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chrism.json

import io.circe.generic.extras.Configuration
import io.circe.generic.extras.decoding.ConfiguredDecoder
import io.circe.generic.extras.encoding.ConfiguredAsObjectEncoder
import io.circe.{parser, Decoder, Encoder, Json, Printer}
import shapeless.Lazy

trait CirceSerializable[A] {

  protected def encode: Json

  final def toJson: Json = encode.deepDropNullValues

  final def printJson()(implicit encoder: Encoder[A]): String = CirceJsonPrinter.printJson(toJson)
}

private[this] object CirceJsonPrinter {

  private[this] val JsonPrinter: Printer = Printer(true, "")

  def printJson(json: Json): String = JsonPrinter.print(json)
}

trait CirceJsonSerDeCompanionLike[A] {

  import com.chrism.json.CirceUtils.implicits._

  @transient
  implicit final lazy val config: Configuration = defaultConfig
  @transient
  implicit final lazy val encoder: Encoder[A] = configureEncoder(CirceUtils.deriveConfiguredEncoder[A](encode))
  @transient
  implicit final lazy val decoder: Decoder[A] = configureDecoder(CirceUtils.deriveConfiguredDecoder[A](decode))

  protected def encode: Lazy[ConfiguredAsObjectEncoder[A]]

  protected def decode: Lazy[ConfiguredDecoder[A]]

  protected /* overridable */ def defaultConfig: Configuration = Configuration.default.withDefaults

  protected /* overridable */ def configureEncoder(enc: Encoder[A]): Encoder[A] = {
    val fields = renameEncodedFieldNames
    if (fields.isEmpty) enc
    else enc.renameAllFields(fields.toSeq)
  }

  protected /* overridable */ def configureDecoder(dec: Decoder[A]): Decoder[A] = {
    val fields = renameDecodedFieldNames
    if (fields.isEmpty) dec
    else dec.renameAllFields(fields.toSeq)
  }

  protected /* overridable */ def renameEncodedFieldNames: Map[String, String] = Map.empty

  protected /* overridable */ def renameDecodedFieldNames: Map[String, String] = Map.empty

  def decodeJson(json: String): Either[io.circe.Error, A] = parser.decode(json)

  def fromJson(json: String): A =
    decodeJson(json) match {
      case Left(err)  => throw err
      case Right(obj) => obj
    }

  def fromJsonOrNone(json: String): Option[A] =
    decodeJson(json) match {
      case Right(obj) => Some(obj)
      case _          => None
    }
}

trait CirceCamelCasedJsonSerDeCompanionLike[A] extends CirceJsonSerDeCompanionLike[A]

trait CirceSnakeCasedJsonSerDeCompanionLike[A] extends CirceJsonSerDeCompanionLike[A] {

  override protected def defaultConfig: Configuration = super.defaultConfig.withSnakeCaseMemberNames
}
