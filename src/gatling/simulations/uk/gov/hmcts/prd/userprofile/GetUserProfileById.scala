package uk.gov.hmcts.prd.userprofile

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import scala.concurrent.duration._
import com.typesafe.config.{Config, ConfigFactory}
object GetUserProfileById {

  val config: Config = ConfigFactory.load()

  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenInternal()

  val GetByIdMin = config.getString("profile.getByIdMin").toInt

  val GetByIdMax = config.getString("profile.getByIdMax").toInt

  val getUserProfileByID = exec(http("TC17_ReferenceData_GETUserProfileByID")
    .get("/v1/userprofile/${User_ID}/roles")
      .header("ServiceAuthorization", s2sToken)
      .header("Authorization", IdAMToken)
      .header("Content-Type", "application/json")
    .check(jsonPath("$.email").saveAs("User_Email_ID"))
      .check(status in (200,201)))
    .pause(GetByIdMin seconds, GetByIdMax seconds)
}
