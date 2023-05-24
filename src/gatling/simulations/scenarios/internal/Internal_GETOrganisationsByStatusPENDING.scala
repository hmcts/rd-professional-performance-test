package scenarios.internal

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object Internal_GETOrganisationsByStatusPENDING {

  val config: Config = ConfigFactory.load()

  val GETOrganisationsByStatusPENDING = 
  
    exec(http("RD11_Internal_GetOrganizationsByStatusPENDING")
      .get("/refdata/internal/v1/organisations?status=PENDING")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "Bearer #{s2sToken}")
      .header("Content-Type", "application/json")
      .check(status is 200))

    .pause(Environment.thinkTime)
}