package uk.gov.hmcts.prd.util

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util.Environment._

import scala.concurrent.duration._
import scala.util.Random

object CreateUser {

  val config: Config = ConfigFactory.load()
  private val rng: Random = new Random()
  private def firstName(): String = rng.alphanumeric.take(20).mkString
  private def lastName(): String = rng.alphanumeric.take(20).mkString
  private def email(): String = rng.alphanumeric.take(15).mkString + "@prdfunctestuser.com"
  private def password(): String = rng.alphanumeric.take(20).mkString

  val userString = "{\"email\": \"${Email}\", \"forename\": \"${FirstName}\", \"password\": \"${Password}\", \"surname\": \"${LastName}\"}"

  val createUser = exec(_.setAll(
    ("FirstName",firstName()),
    ("LastName",lastName()),
    ("Email", email()),
    ("Password",password()),
  ))

    .exec(http("IDAM_CreateUser")
      .post("https://idam-api." + env + ".platform.hmcts.net/testing-support/accounts")
      .header("Authorization", "Bearer ${accessToken}")
      .header("ServiceAuthorization", "Bearer ${s2sToken}")
      .header("Content-Type", "application/json")
      .body(StringBody(userString))
      .check(status is 201))
    .pause(thinkTime)

}
