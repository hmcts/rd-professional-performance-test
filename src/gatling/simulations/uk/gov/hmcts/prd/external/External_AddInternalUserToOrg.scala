package uk.gov.hmcts.prd.external
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._

import scala.util.Random
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object External_AddInternalUserToOrg {
  private val rng: Random = new Random()

  private def internalUser_firstName(): String = rng.alphanumeric.take(20).mkString

  private def internalUser_lastName(): String = rng.alphanumeric.take(20).mkString

  private def internalUser_Email(): String = rng.alphanumeric.take(15).mkString

  val config: Config = ConfigFactory.load()
  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenExternal()

  val AddUsrMin = config.getString("external.addUsrMin").toInt

  val AddUsrMax = config.getString("external.addUsrMax").toInt

  val addInternalUserString = "{\n \"firstName\": \"Kapil ${InternalUser_FirstName}\",\n \"lastName\": \"Jain ${InternalUser_LastName}\",\n \"email\": \"Kapil.${InternalUser_Email}@gmail.com\",\n \"roles\": [\n   \"pui-user-manager\",\n   \"pui-organisation-manager\"\n ]\n,\n        \"jurisdictions\": [\n    {\n      \"id\": \"Divorce\"\n    },\n    {\n      \"id\": \"SSCS\"\n    },\n    {\n      \"id\": \"Probate\"\n    },\n    {\n      \"id\": \"Public Law\"\n    },\n    {\n      \"id\": \"Bulk Scanning\"\n    },\n    {\n      \"id\": \"Immigration & Asylum\"\n    },\n    {\n      \"id\": \"Civil Money Claims\"\n    },\n    {\n      \"id\": \"Employment\"\n    },\n    {\n      \"id\": \"Family public law and adoption\"\n    },\n    {\n      \"id\": \"Civil enforcement and possession\"\n    }\n  ]\n}"


  val AddInternalUserToOrg = repeat(1) {
    exec(_.setAll(("InternalUser_FirstName", internalUser_firstName()), ("InternalUser_LastName", internalUser_lastName()), ("InternalUser_Email", internalUser_Email()))).exec(http("RD14_External_AddInternalUserToOrganisation").post("/refdata/external/v1/organisations/users/").header("ServiceAuthorization", s2sToken).header("Authorization", IdAMToken).body(StringBody(addInternalUserString)).header("Content-Type", "application/json").check(status is 201)).pause(AddUsrMin seconds, AddUsrMax seconds)
  }
}


