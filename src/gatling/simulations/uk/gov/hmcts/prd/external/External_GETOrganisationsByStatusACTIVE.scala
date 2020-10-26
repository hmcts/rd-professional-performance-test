package uk.gov.hmcts.prd.external

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._

import scala.concurrent.duration._

object External_GETOrganisationsByStatusACTIVE {

  val config: Config = ConfigFactory.load()

  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenInternal()

  val GetActiveOrgMin = config.getString("internal.getActiveOrgMin").toInt

  val GetActiveOrgMax = config.getString("internal.getActiveOrgMax").toInt

  val GETOrganisationsByStatusACTIVE = exec(http("RD17_External_GetOrganizationsByStatusACTIVE")
    .get("/refdata/external/v1/organisations/status/ACTIVE")
    .header("Authorization", "Bearer ${accessToken}")
    .header("ServiceAuthorization", "Bearer ${s2sToken}")
    .header("Content-Type", "application/json")
    .check(status is 200))
    .pause(GetActiveOrgMin seconds, GetActiveOrgMax seconds)
}