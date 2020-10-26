package uk.gov.hmcts.prd.util

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random

object CreateUser {

  val config: Config = ConfigFactory.load()
  private val rng: Random = new Random()

  private def userEmail(): String = rng.alphanumeric.take(15).mkString + "@prdfunctestuser.com"

  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenExternal()

  val GetPbasMin = config.getString("external.getPbasMin").toInt

  val GetPbasMax = config.getString("external.getPbasMax").toInt

  val userString = "{\"email\": \"${UserEmail}\", \"forename\": \"John\", \"password\": \"Testing1234\", \"surname\": \"Smith\"}"

  val createUser = exec(_.setAll(
    ("UserEmail", userEmail())
  ))

    .exec(http("IDAM_CreateUser")
      .post("https://idam-api.perftest.platform.hmcts.net/testing-support/accounts")
      //.post("https://idam-api.aat.platform.hmcts.net/testing-support/accounts")
      .header("Authorization", "Bearer ${accessToken}")
      .header("ServiceAuthorization", "Bearer ${s2sToken}")
      .header("Content-Type", "application/json")
      .body(StringBody(userString))
      .check(status is 201))
    .pause(GetPbasMin seconds, GetPbasMax seconds)

}
