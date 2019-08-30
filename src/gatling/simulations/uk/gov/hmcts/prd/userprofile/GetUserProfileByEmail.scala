package uk.gov.hmcts.prd.userprofile

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import scala.concurrent.duration._
import com.typesafe.config.{Config, ConfigFactory}
object GetUserProfileByEmail {


  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenInternal()

  val config: Config = ConfigFactory.load()

  val GetByEmailMin = config.getString("profile.getByEmailMin").toInt

  val GetByEmailMax = config.getString("profile.getByEmailMax").toInt

  val getUserProfileByEmail = exec(http("TC18_ReferenceData_GetUserProfileByEmail")
    .get("/v1/userprofile/roles?email=${User_Email_ID}")
      .header("ServiceAuthorization", s2sToken)
      .header("Authorization", IdAMToken)
      .header("Content-Type", "application/json")
      .check(status in (200,201)))
    .pause(GetByEmailMin seconds, GetByEmailMin seconds)
}
