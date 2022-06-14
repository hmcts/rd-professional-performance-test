package uk.gov.hmcts.prd.location

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object LRD_ApiController {

  val config: Config = ConfigFactory.load()
  val regions = csv("Regions.csv").random
  val services = csv("Services.csv").random

  val GetBuildingLocations = 

    feed(regions)

    .exec(http("LD01_GetBuildingLocations")
      .get(Environment.lrdUrl + "/refdata/location/building-locations")
      .header("Authorization", "Bearer ${accessToken}")
      .header("ServiceAuthorization", "Bearer ${rd_location_ref_apiBearerToken}")
      .header("accept", "application/json")
      .formParam("region_id", "${region}")
      )

    .pause(Environment.thinkTime)

  val GetOrgServices = 

    feed(services)

    .exec(http("LD02_GetOrgServices")
      .get(Environment.lrdUrl + "/refdata/location/orgServices")
      .header("Authorization", "Bearer ${accessToken}")
      .header("ServiceAuthorization", "Bearer ${rd_location_ref_apiBearerToken}")
      .header("accept", "application/json")
      .formParam("ccdServiceNames", "${service}")
      .formParam("ccdCaseType", "${caseType}")
      )

  .pause(Environment.thinkTime)

  val GetRegions =

    feed(regions)

    .exec(http("LD03_GetRegions")
      .get(Environment.lrdUrl + "/refdata/location/regions")
      .header("Authorization", "Bearer ${accessToken}")
      .header("ServiceAuthorization", "Bearer ${rd_location_ref_apiBearerToken}")
      .header("accept", "application/json")
      .formParam("region", "3")
      )

    .pause(Environment.thinkTime)

}