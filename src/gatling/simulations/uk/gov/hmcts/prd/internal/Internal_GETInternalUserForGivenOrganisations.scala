package uk.gov.hmcts.prd.internal

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object Internal_GETInternalUserForGivenOrganisations {

  val config: Config = ConfigFactory.load()

  val OrgIdData = csv("prdIntOrgIDs.csv").circular

  val GetIntUsrByOrgMin = config.getString("internal.getIntUsrByOrgMin").toInt
  val GetIntUsrByOrgMax = config.getString("internal.getIntUsrByOrgMax").toInt

  val GETInternalUserForGivenOrganisations = feed(OrgIdData)

    .exec(http("RD06_Internal_GetInternalUserForGivenOrganisation")
      .get("/refdata/internal/v1/organisations/${NewPendingOrg_Id}/users?showdeleted=false&rolesRequired=false")
      .header("Authorization", "Bearer ${accessToken}")
      .header("ServiceAuthorization", "Bearer ${s2sToken}")
      .header("Content-Type", "application/json")
      .check(status is 200))
    .pause(GetIntUsrByOrgMin seconds, GetIntUsrByOrgMax seconds)
}
