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
object Dependencies {

  import sbt._

  private val CirceGroupId: String = "io.circe"

  private val CirceVersion = "0.13.0"

  private val ScalacheckVersion: String = "1.14.0"
  private val ScalatestVersion: String = "3.0.8"
  private val Specs2CoreVersion: String = "4.7.0"

  val Commons4s: ModuleID = "com.chrism" %% "commons4s" % "1.0.0"

  val CirceCore: ModuleID = CirceGroupId %% "circe-core" % CirceVersion
  val CirceGeneric: ModuleID = CirceGroupId %% "circe-generic" % CirceVersion
  val CirceParser: ModuleID = CirceGroupId %% "circe-parser" % CirceVersion
  val CirceGenericExtras: ModuleID = CirceGroupId %% "circe-generic-extras" % CirceVersion

  val Scalacheck: ModuleID = "org.scalacheck" %% "scalacheck" % ScalacheckVersion
  val Scalatest: ModuleID = "org.scalatest" %% "scalatest" % ScalatestVersion
  val Specs2Core: ModuleID = "org.specs2" %% "specs2-core" % Specs2CoreVersion
}
