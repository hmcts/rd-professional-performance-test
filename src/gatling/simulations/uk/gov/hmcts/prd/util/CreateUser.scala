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
  private def email(): String = rng.alphanumeric.take(15).mkString + "@prdperftestuser.com"
  private def password(): String = rng.alphanumeric.take(20).mkString

  val userString = "{\"email\": \"${Email}\", \"forename\": \"${FirstName}\", \"password\": \"P${Password}123\", \"surname\": \"${LastName}\"}"

  val createUser = exec(_.setAll(
    ("FirstName",firstName()),
    ("LastName",lastName()),
    ("Email", email()),
    ("Password",password()),
  ))

    .exec(http("IDAM_CreateUser")
      .post("https://idam-api.${env}.platform.hmcts.net/testing-support/accounts")
      .header("Content-Type", "application/json")
      .body(StringBody(userString))
      .check(status is 201))
      .exitHereIfFailed

      // .exec( session => {
      //   println("the email is "+session("Email").as[String])
      //   session
      // })
      
    .pause(7)

  val deleteUser = 

    exec(http("IDAM_DeleteUser")
      .delete("https://idam-api.${env}.platform.hmcts.net/testing-support/accounts/${Email}")
      .header("Content-Type", "application/json")
      .check(status is 204))    

}
