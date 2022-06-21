package uk.gov.hmcts.prd.internal

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._

object External_DeletePBA {

  val config: Config = ConfigFactory.load()

  val DeletePBA = 
  
    exec(http("RD26_External_DeletePBA")
      .delete("/refdata/external/v1/organisations/pba")
      .header("Authorization", "Bearer ${accessToken}")
      .header("ServiceAuthorization", "Bearer ${s2sToken}")
      .header("Content-Type", "application/json")
      .body(ElFileBody("bodies/external/DeletePBA.json")))

    .pause(Environment.thinkTime)
}