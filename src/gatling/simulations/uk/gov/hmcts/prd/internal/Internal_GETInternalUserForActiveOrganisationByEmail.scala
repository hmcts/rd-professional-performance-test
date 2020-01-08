package uk.gov.hmcts.prd.internal

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._
object Internal_GETInternalUserForActiveOrganisationByEmail {

  val config: Config = ConfigFactory.load()

  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenInternal()

  val OrgIdData = csv("prdIntOrgIDs.csv").circular

  val GetIntUsrByEmailMin = config.getString("internal.getIntUsrByEmailMin").toInt
  val GetIntUsrByEmailMax = config.getString("internal.getIntUsrByEmailMax").toInt

  val GETInternalUserForActiveOrganisationByEmail = exec(http("RD10_Internal_GetInternalUserForActiveOrganisationByEmailAddress")
    .get("/refdata/internal/v1/organisations/8BVPTEI/users?email=" + "tpAkWZF296Se4yjA1L@email.co.uk" + "")
    .header("ServiceAuthorization", s2sToken)
    .header("Authorization", IdAMToken)
    .header("Content-Type", "application/json")
    .check(status is 200))
    .pause(GetIntUsrByEmailMin seconds, GetIntUsrByEmailMax seconds)
}
