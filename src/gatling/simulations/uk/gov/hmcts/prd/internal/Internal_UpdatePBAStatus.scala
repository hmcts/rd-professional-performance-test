package uk.gov.hmcts.prd.internal

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object Internal_UpdatePBAStatus {

  val config: Config = ConfigFactory.load()

  val Update = 
  
    exec(http("RD16_Internal_UpdatePBAStatus")
      .put("/refdata/internal/v1/organisations/${NewPendingOrg_Id}/pba/status")
      .header("Authorization", "Bearer ${accessToken}")
      .header("ServiceAuthorization", "Bearer ${s2sToken}")
      .header("Content-Type", "application/json")
      .body(ElFileBody("bodies/internal/UpdatePBAStatus.json")))
      
    .pause(Environment.thinkTime)
}