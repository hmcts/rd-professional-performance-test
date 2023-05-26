package scenarios.location

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object LRD_VenueController {

  val config: Config = ConfigFactory.load()
  val regions = csv("Regions.csv").random
  val searches = csv("Searches.csv").random

  val GetCourtVenues = 

    feed(regions)

    .exec(http("LD04_GetCourtVenues")
      .get(Environment.lrdUrl + "/refdata/location/court-venues")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "Bearer #{rd_location_ref_apiBearerToken}")
      .header("accept", "application/json")
      .formParam("region_id", "#{region}")
      )

    .pause(Environment.thinkTime)

  val CourtVenueSearch = 

    feed(searches)

    .exec(http("LD05_SearchCourtVenues")
      .get(Environment.lrdUrl + "/refdata/location/court-venues/venue-search?search-string=#{query}")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "Bearer #{rd_location_ref_apiBearerToken}")
      .header("accept", "application/json")
      )

}