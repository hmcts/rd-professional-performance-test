package uk.gov.hmcts.prd.external
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._

import scala.util.Random
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object External_AddInternalUserToOrg {

  val config: Config = ConfigFactory.load()
  
  val AddInternalUserToOrg = 
    
    exec(http("RD21_External_AddInternalUserToOrganisation")
      .post("/refdata/external/v1/organisations/users/")
      .header("Authorization", "Bearer ${accessToken}")
      .header("ServiceAuthorization", "Bearer ${s2sToken}")
      .body(ElFileBody("bodies/external/AddUserToOrg.json"))
      .header("Content-Type", "application/json")
      .check(status is 201)
      .check(jsonPath("$.userIdentifier").saveAs("userId")))
  
    .pause(Environment.thinkTime)
}