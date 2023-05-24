package scenarios.internal

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object Internal_GETAllOrganisation {

  val config: Config = ConfigFactory.load()

  val GETAllOrganisation = 
  
    exec(http("RD08_Internal_GetAllOrganizations")
      .get("/refdata/internal/v1/organisations")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "Bearer #{s2sToken}")
      .header("Content-Type", "application/json")
      .check(status is 200))
    
    .pause(Environment.thinkTime)
}