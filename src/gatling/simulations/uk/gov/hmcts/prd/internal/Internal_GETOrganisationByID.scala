package uk.gov.hmcts.prd.internal

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object Internal_GETOrganisationByID {

  val config: Config = ConfigFactory.load()
  val OrgIdData = csv("prdIntOrgIDs.csv").circular

  val GETOrganisationByID = 
  
    feed(OrgIdData)

    .exec(http("RD04_Internal_GetOrganizationsByID")
      .get("/refdata/internal/v1/organisations?id=${NewPendingOrg_Id}")
      .header("Authorization", "Bearer ${accessToken}")
      .header("ServiceAuthorization", "Bearer ${s2sToken}")
      .header("Content-Type", "application/json")
      .check(status is 200))

    .pause(Environment.thinkTime)
}