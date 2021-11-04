package uk.gov.hmcts.prd.external

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._
object External_GETInternalUserForGivenOrganisations {

  val config: Config = ConfigFactory.load()

  val GetIntUsrByOrgIdMin = config.getString("external.getIntUsrByOrgIdMin").toInt
  val GetIntUsrByOrgIdMax = config.getString("external.getIntUsrByOrgIdMax").toInt

  val GETInternalUserForGivenOrganisations = 
  
  exec(http("RD18_External_GetInternalUserForGivenOrganisation")
    .get("/refdata/external/v1/organisations/users?showdeleted=True")
    .header("Authorization", "Bearer ${accessToken}")
    .header("ServiceAuthorization", "Bearer ${s2sToken}")
    .header("Content-Type", "application/json")
    .check(status is 200))

  .pause(Environment.thinkTime)
}