package uk.gov.hmcts.prd.external

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._
object External_GETInternalUserForActiveOrganisationByEmail {

  val config: Config = ConfigFactory.load()

  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenExternal()

  val GetIntUsrByOrgEmailMin = config.getString("external.getIntUsrByOrgEmailMin").toInt

  val GetIntUsrByOrgEmailMax = config.getString("external.getIntUsrByOrgEmailMax").toInt

  val GETInternalUserForActiveOrganisationByEmail = exec(http("RD16_External_GetInternalUserForActiveOrganisationByEmailAddress")
    .get("/refdata/external/v1/organisations/users?email=" + "tpalzz3balrmkbr0fa@email.co.uk" + "")
    .header("ServiceAuthorization", s2sToken)
    .header("Authorization", IdAMToken)
    .header("Content-Type", "application/json")
    .check(status is 200))
    .pause(GetIntUsrByOrgEmailMin seconds, GetIntUsrByOrgEmailMax seconds)
}