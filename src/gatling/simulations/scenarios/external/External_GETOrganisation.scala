package scenarios.external

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object External_GETOrganisation {

  val config: Config = ConfigFactory.load()

  val GETOrganisation = 

    repeat(100){ //100
    
      exec(http("RD20_External_GetOrganizations")
        .get("/refdata/external/v1/organisations")
        .header("Authorization", "Bearer #{accessToken}")
        .header("ServiceAuthorization", "Bearer #{s2sToken}")
        .header("Content-Type", "application/json")
        .check(status is 200))

      .pause(Environment.thinkTime)
    }
}