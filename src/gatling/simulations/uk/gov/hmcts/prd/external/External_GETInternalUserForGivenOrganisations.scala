package uk.gov.hmcts.prd.external

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object External_GETInternalUserForGivenOrganisations {

  val config: Config = ConfigFactory.load()

  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenExternal()

  val GetIntUsrByOrgIdMin = config.getString("external.getIntUsrByOrgIdMin").toInt

  val GetIntUsrByOrgIdMax = config.getString("external.getIntUsrByOrgIdMax").toInt

  val GETInternalUserForGivenOrganisations = exec(http("RD18_External_GetInternalUserForGivenOrganisation")
    .get("/refdata/external/v1/organisations/users?showdeleted=True")
    .header("Authorization", "Bearer ${accessToken}")
    .header("ServiceAuthorization", "Bearer ${s2sToken}")
    .header("Content-Type", "application/json")
    .check(status is 200))
    .pause(GetIntUsrByOrgIdMin seconds, GetIntUsrByOrgIdMax seconds)

  /*val GETUsersByOrganisation = exec(http("GetUsersByOrg").get("/refdata/internal/v1/organisations/YS0C4QG/users?returnRoles=true").header("ServiceAuthorization", "${s2sToken}").header("Authorization", "${accessToken}").header("Content-Type", "application/json").check(status is 200)).pause(10)*/

  //val GETUsersByOrganisation = exec(http("GetUsersByOrg").get("http://rd-professional-api-perftest.service.core-compute-perftest.internal/refdata/internal/v1/organisations/DFV84Q8/users?returnRoles=false").header("ServiceAuthorization", "${s2sToken}").header("Authorization", " Bearer ${accessToken}").header("Content-Type", "application/json").check(status is 200)).pause(10)
}
