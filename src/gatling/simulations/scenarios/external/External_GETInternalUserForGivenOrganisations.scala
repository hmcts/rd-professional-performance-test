package scenarios.external

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object External_GETInternalUserForGivenOrganisations {

  val config: Config = ConfigFactory.load()

  val GETInternalUserForGivenOrganisations = 
  
  exec(http("RD22_External_GetInternalUserForGivenOrganisation")
    .get("/refdata/external/v1/organisations/users?showdeleted=True")
    .header("Authorization", "Bearer #{accessToken}")
    .header("ServiceAuthorization", "Bearer #{s2sToken}")
    .header("Content-Type", "application/json")
    .check(status is 200))

  .pause(Environment.thinkTime)
}