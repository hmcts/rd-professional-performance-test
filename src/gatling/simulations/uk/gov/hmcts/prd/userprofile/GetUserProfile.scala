package uk.gov.hmcts.prd.userprofile

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object GetUserProfile {

  val config: Config = ConfigFactory.load()

  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenInternal()

  val GetMin = config.getString("profile.getMin").toInt

  val GetMax = config.getString("profile.getMax").toInt

  val getUserProfile = exec(http("TC17_ReferenceData_GETUserProfile")
    .get("/v1/userprofile?userId=${User_ID}")
      .header("ServiceAuthorization", s2sToken)
      .header("Authorization", IdAMToken)
      .header("Content-Type", "application/json")
      .check(status in (200,201)))

    .pause(GetMin seconds, GetMax seconds)
}
