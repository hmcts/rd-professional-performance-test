package uk.gov.hmcts.prd.internal

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object Internal_GETPbas {

  val config: Config = ConfigFactory.load()

  val GETPbas = 

    repeat(1) {
  
      exec(http("RD12_Internal_RetrievesOrganisationsPaymentAccounts")
        .get("/refdata/internal/v1/organisations/pbas?email=${adminEmail}") //Email
        .header("Authorization", "Bearer ${accessToken}")
        .header("ServiceAuthorization", "Bearer ${s2sToken}")
        .header("UserEmail", "${adminEmail}") //Email
        .header("Content-Type", "application/json"))
        
      .pause(Environment.thinkTime)

      .exec(http("RD13_Internal_RetrievesOrganisationsPBAStatus")
        .get("/refdata/internal/v1/organisations/pba/accepted")
        .header("Authorization", "Bearer ${accessToken}")
        .header("ServiceAuthorization", "Bearer ${s2sToken}")
        .header("UserEmail", "${adminEmail}") //Email
        .header("Content-Type", "application/json"))
        
      .pause(Environment.thinkTime)
    }
}