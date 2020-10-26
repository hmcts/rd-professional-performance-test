package uk.gov.hmcts.prd.internal

import com.typesafe.config.{Config, ConfigFactory}

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random

object Internal_CreateUser {

  val config: Config = ConfigFactory.load()
  private val rng: Random = new Random()
  private def companyEmail(): String = rng.alphanumeric.take(15).mkString + "@email.com"
  private def companyEmail2(): String = rng.alphanumeric.take(15).mkString + "@email.com"

  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenExternal()

  val createOrganisation = exec(_.setAll(
    ("CompanyEmail",companyEmail()),
    ("InternalUser_Email",companyEmail2())
  ))

  val GetPbasMin = config.getString("external.getPbasMin").toInt

  val GetPbasMax = config.getString("external.getPbasMax").toInt

  val userString = "{\"email\": \"${CompanyEmail}\", \"forename\": \"John\", \"password\": \"Testing1234\", \"surname\": \"Smith\"}"

  val userString2 = "{\"email\": \"${InternalUser_Email}\", \"forename\": \"James\", \"password\": \"Testing1234\", \"surname\": \"Brown\"}"

  val createUser = exec(http("RD19_CreateUser")
   .post("https://idam-api.aat.platform.hmcts.net/testing-support/accounts")
    .header("Authorization", "Bearer ${accessToken}")
    .header("ServiceAuthorization", "Bearer ${s2sToken}")
    .header("Content-Type", "application/json")
    .body(StringBody(userString))
    .check(status is 201))
    .pause(GetPbasMin seconds, GetPbasMax seconds)

  val createUser2 = exec(http("RD19_CreateUser2")
    .post("https://idam-api.aat.platform.hmcts.net/testing-support/accounts")
    .header("Authorization", "Bearer ${accessToken}")
    .header("ServiceAuthorization", "Bearer ${s2sToken}")
    .header("Content-Type", "application/json")
    .body(StringBody(userString2))
    .check(status is 201))
    .pause(GetPbasMin seconds, GetPbasMax seconds)
}
