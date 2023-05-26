package utils

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment._
import scala.concurrent.duration._
import scala.util.Random

object CreateUser {

  val config: Config = ConfigFactory.load()
  private val rng: Random = new Random()
  private def firstName(): String = rng.alphanumeric.take(20).mkString
  private def lastName(): String = rng.alphanumeric.take(20).mkString
  private def email(): String = rng.alphanumeric.take(15).mkString + "@prdperftestuser.com"
  // private def password(): String = rng.alphanumeric.take(20).mkString

  val createAdminUser = 
  
    exec(_.setAll(
      ("FirstName",firstName()),
      ("LastName",lastName()),
      ("Email", email())
    ))

    .exec(http("IDAM_CreateSuperUser")
      .post("https://idam-api.#{env}.platform.hmcts.net/testing-support/accounts")
      .header("Content-Type", "application/json")
      .body(ElFileBody("bodies/idam/Idam_CreateSuperUser.json"))
      .check(status is 201)
      .check(jsonPath("$.email").saveAs("adminEmail")))
      .exitHereIfFailed

      // .exec( session => {
      //   println("the email is "+session("adminEmail").as[String])
      //   session
      // })
      
    .pause(7)

  val createUser = 

    exec(_.setAll(
      ("FirstName",firstName()),
      ("LastName",lastName()),
      ("Email", email())
    ))

    .exec(http("IDAM_CreateUser")
      .post("https://idam-api.#{env}.platform.hmcts.net/testing-support/accounts")
      .header("Content-Type", "application/json")
      .body(ElFileBody("bodies/idam/Idam_CreateUser.json"))
      .check(status is 201)
      .check(jsonPath("$.email").saveAs("userEmail"))
      .check(jsonPath("$.forename").saveAs("userFirstname"))
      .check(jsonPath("$.surname").saveAs("userLastname"))
      )
      .exitHereIfFailed

      // .exec( session => {
      //   println("the email is "+session("userEmail").as[String])
      //   session
      // })
      
    .pause(7)

  val deleteUser = 

    exec(http("IDAM_DeleteUser")
      .delete("https://idam-api.#{env}.platform.hmcts.net/testing-support/accounts/#{Email}")
      .header("Content-Type", "application/json")
      .check(status is 204))

  val deleteNewUser = 

    exec(http("IDAM_DeleteUser")
      .delete("https://idam-api.#{env}.platform.hmcts.net/testing-support/accounts/#{userEmail}")
      .header("Content-Type", "application/json")
      .check(status is 204))

  val deleteAdminUser = 

    exec(http("IDAM_DeleteUser")
      .delete("https://idam-api.#{env}.platform.hmcts.net/testing-support/accounts/#{adminEmail}")
      .header("Content-Type", "application/json")
      .check(status is 204))
}
