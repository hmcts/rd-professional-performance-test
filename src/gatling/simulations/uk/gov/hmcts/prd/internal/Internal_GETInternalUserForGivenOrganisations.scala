package uk.gov.hmcts.prd.internal

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object Internal_GETInternalUserForGivenOrganisations {

  val config: Config = ConfigFactory.load()

  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenInternal()

  val OrgIdData = csv("prdIntOrgIDs.csv").circular

  val GetIntUsrByOrgMin = config.getString("internal.getIntUsrByOrgMin").toInt
  val GetIntUsrByOrgMax = config.getString("internal.getIntUsrByOrgMax").toInt

  val GETInternalUserForGivenOrganisations = feed(OrgIdData)

    .exec(http("RD09_Internal_GetInternalUserForGivenOrganisation")
      .get("/refdata/internal/v1/organisations/${PRD_Org_ID}/users?showdeleted=false&rolesRequired=false")
      .header("ServiceAuthorization", s2sToken)
      .header("Authorization", IdAMToken)
      .header("Content-Type", "application/json")
      .check(status is 200))
    .pause(GetIntUsrByOrgMin seconds, GetIntUsrByOrgMax seconds)
}
