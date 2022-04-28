package uk.gov.hmcts.prd.external

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._
object External_GETOrganisation {

  val config: Config = ConfigFactory.load()

  // val GetOrgByOrgIdMin = config.getString("external.getOrgByOrgIdMin").toInt
  // val GetOrgByOrgIdMax = config.getString("external.getOrgByOrgIdMax").toInt

  val GETOrganisation = 

    repeat(50){ 
    
      exec(http("RD20_External_GetOrganizations")
        .get("/refdata/external/v1/organisations")
        .header("Authorization", "Bearer ${accessToken}")
        .header("ServiceAuthorization", "Bearer ${s2sToken}")
        .header("Content-Type", "application/json")
        .check(status is 200))

      .pause(2)
    }
}