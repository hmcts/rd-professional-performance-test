package scenarios.internal

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils._

object Internal_DeleteOrganisation {

  val config: Config = ConfigFactory.load()

  val DeleteOrganisation = 
  
    exec(http("RD02_Internal_DeleteOrganizations")
      .delete("/refdata/internal/v1/organisations/#{NewPendingOrg_Id}")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "Bearer #{s2sToken}")
      .header("Content-Type", "application/json")
      .check(status is 204))

    .pause(Environment.thinkTime)
}