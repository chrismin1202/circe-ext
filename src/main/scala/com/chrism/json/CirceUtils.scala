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

import io.circe.generic.extras.decoding.ConfiguredDecoder
import io.circe.generic.extras.encoding.ConfiguredAsObjectEncoder
import io.circe.generic.extras.semiauto
import io.circe.{Decoder, Encoder, JsonObject}
import shapeless.Lazy

object CirceUtils {

  def renameJsonField(json: JsonObject, oldName: String, newName: String): JsonObject =
    json(oldName).map(json.add(newName, _).remove(oldName)).getOrElse(json)

  def renameAllJsonFields(json: JsonObject, oldNewNames: Seq[(String, String)]): JsonObject =
    oldNewNames.foldLeft(json)((j, t) => renameJsonField(j, t._1, t._2))

  def renameJsonFields(json: JsonObject, oldNew: (String, String), more: (String, String)*): JsonObject =
    renameAllJsonFields(json, oldNew +: more)

  def deriveConfiguredEncoder[A](implicit encode: Lazy[ConfiguredAsObjectEncoder[A]]): Encoder[A] =
    semiauto.deriveConfiguredEncoder[A]

  def configureEncoderWithAllFieldsRenamed[A](encoder: Encoder[A], oldNewNames: Seq[(String, String)]): Encoder[A] =
    encoder.mapJson(_.mapObject(obj => renameAllJsonFields(obj, oldNewNames)))

  def configureEncoderWithFieldsRenamed[A](
    encoder: Encoder[A],
    oldNew: (String, String),
    more: (String, String)*
  ): Encoder[A] =
    configureEncoderWithAllFieldsRenamed(encoder, oldNew +: more)

  def deriveConfiguredEncoderWithAllFieldsRenamed[A](
    oldNewNames: Seq[(String, String)]
  )(
    implicit
    encode: Lazy[ConfiguredAsObjectEncoder[A]]
  ): Encoder[A] =
    deriveConfiguredEncoder[A].mapJson(_.mapObject(obj => renameAllJsonFields(obj, oldNewNames)))

  def deriveConfiguredEncoderWithFieldsRenamed[A](
    oldNew: (String, String),
    more: (String, String)*
  )(
    implicit
    encode: Lazy[ConfiguredAsObjectEncoder[A]]
  ): Encoder[A] =
    deriveConfiguredEncoderWithAllFieldsRenamed[A](oldNew +: more)

  def deriveConfiguredDecoder[A](implicit decode: Lazy[ConfiguredDecoder[A]]): Decoder[A] =
    semiauto.deriveConfiguredDecoder[A]

  def configureDecoderWithAllFieldsRenamed[A](decoder: Decoder[A], oldNewNames: Seq[(String, String)]): Decoder[A] =
    decoder.prepare(_.withFocus(_.mapObject(renameAllJsonFields(_, oldNewNames))))

  def configureDecoderWithFieldsRenamed[A](
    decoder: Decoder[A],
    oldNew: (String, String),
    more: (String, String)*
  ): Decoder[A] =
    configureDecoderWithAllFieldsRenamed(decoder, oldNew +: more)

  def deriveConfiguredDecoderWithAllFieldsRenamed[A](
    oldNewNames: Seq[(String, String)]
  )(
    implicit
    decode: Lazy[ConfiguredDecoder[A]]
  ): Decoder[A] =
    deriveConfiguredDecoder[A].prepare(_.withFocus(_.mapObject(renameAllJsonFields(_, oldNewNames))))

  def deriveConfiguredDecoderWithFieldsRenamed[A](
    oldNew: (String, String),
    more: (String, String)*
  )(
    implicit
    decode: Lazy[ConfiguredDecoder[A]]
  ): Decoder[A] =
    deriveConfiguredDecoderWithAllFieldsRenamed[A](oldNew +: more)

  object implicits {

    implicit final class JsonObjectOps(json: JsonObject) {

      def renameAllFields(oldNewNames: Seq[(String, String)]): JsonObject = renameAllJsonFields(json, oldNewNames)

      def renameFields(oldNew: (String, String), more: (String, String)*): JsonObject = renameAllFields(oldNew +: more)
    }

    implicit final class EncoderOps[A](encoder: Encoder[A]) {

      def renameAllFields(oldNewNames: Seq[(String, String)]): Encoder[A] =
        configureEncoderWithAllFieldsRenamed(encoder, oldNewNames)

      def renameFields(oldNew: (String, String), more: (String, String)*): Encoder[A] =
        renameAllFields(oldNew +: more)
    }

    implicit final class DecoderOps[A](decoder: Decoder[A]) {

      def renameAllFields(oldNewNames: Seq[(String, String)]): Decoder[A] =
        configureDecoderWithAllFieldsRenamed(decoder, oldNewNames)

      def renameFields(oldNew: (String, String), more: (String, String)*): Decoder[A] =
        renameAllFields(oldNew +: more)
    }
  }
}
