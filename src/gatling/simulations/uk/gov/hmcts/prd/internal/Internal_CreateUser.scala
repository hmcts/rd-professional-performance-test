package uk.gov.hmcts.prd.internal

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._

import scala.concurrent.duration._

object Internal_CreateUser {

  val config: Config = ConfigFactory.load()

  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenExternal()

  val GetPbasMin = config.getString("external.getPbasMin").toInt

  val GetPbasMax = config.getString("external.getPbasMax").toInt

  val userString = "{\"email\": \"abcdefg14@email.co.uk\", \"forename\": \"John\", \"password\": \"Testing123\", \"surname\": \"Smith\"}"

  val createUser = exec(http("RD19_CreateUser")
   .post("https://idam-api.aat.platform.hmcts.net/testing-support/accounts")
    .header("Authorization", "Bearer ${accessToken}")
    .header("ServiceAuthorization", "Bearer ${s2sToken}")
    .header("Content-Type", "application/json")
    .body(StringBody(userString))
    .check(status is 201))
    .pause(GetPbasMin seconds, GetPbasMax seconds)
}
