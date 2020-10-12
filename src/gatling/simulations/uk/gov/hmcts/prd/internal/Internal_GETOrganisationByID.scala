package uk.gov.hmcts.prd.internal

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._
object Internal_GETOrganisationByID {

  val config: Config = ConfigFactory.load()

  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenInternal()

  val OrgIdData = csv("prdIntOrgIDs.csv").circular

  val GetOrgByOrgIdMin = config.getString("internal.getOrgByOrgIdMin").toInt

  val GetOrgByOrgIdMax = config.getString("internal.getOrgByOrgIdMax").toInt

  val GETOrganisationByID = feed(OrgIdData)

    .exec(http("RD04_Internal_GetOrganizationsByID")
      //.get("/refdata/internal/v1/organisations?id=${PRD_Org_ID}")
      .get("/refdata/internal/v1/organisations?id=M3SMFBI")

      .header("ServiceAuthorization", s2sToken)
      .header("Authorization", IdAMToken)
      .header("Content-Type", "application/json")
      .check(status is 200))
    .pause(GetOrgByOrgIdMin seconds, GetOrgByOrgIdMax seconds)
}
