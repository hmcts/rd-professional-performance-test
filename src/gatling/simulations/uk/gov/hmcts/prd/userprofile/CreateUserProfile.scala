package uk.gov.hmcts.prd.userprofile

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import scala.concurrent.duration._
import scala.util.Random
import com.typesafe.config.{Config, ConfigFactory}
object CreateUserProfile {

  private val rng: Random = new Random()
  val config: Config = ConfigFactory.load()

  private def firstName(): String = rng.alphanumeric.take(20).mkString
  private def lastName(): String = rng.alphanumeric.take(20).mkString

  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenInternal()

  val CreateMin = config.getString("profile.createMin").toInt
  val CreateMax = config.getString("profile.createMax").toInt

  val userProfileString = "{\n  \"email\": \"Kapil${FirstName}.Jain${LastName}@gmail.com\",\n  \"firstName\": \"Kapil${FirstName}\",\n  \"lastName\": \"Jain${LastName}\",\n  \"languagePreference\": \"EN\",\n  \"emailCommsConsent\": true,\n  \"postalCommsConsent\": true,\n  \"userCategory\": \"PROFESSIONAL\",\n  \"userType\": \"EXTERNAL\",\n  \"roles\": [\n    \"pui-case-manager\"\n,\n    \"pui-user-manager\"\n,\n    \"pui-organisation-manager\"\n,\n    \"pui-finance-manager\"\n   ]\n}"


  val createUserProfile = exec(_.setAll(
    ("FirstName",firstName()),
    ("LastName",lastName())
  ))


    .exec(http("TC16_ReferenceData_CreateUserProfile")
        .post("/v1/userprofile")
      .header("ServiceAuthorization", s2sToken)
      .header("Authorization", IdAMToken)
      .body(StringBody(userProfileString))
      .header("Content-Type", "application/json")
      .check(jsonPath("$.idamId").saveAs("User_ID"))
      .check(status in (200,201)))
      .pause(CreateMin seconds, CreateMax seconds)
}
