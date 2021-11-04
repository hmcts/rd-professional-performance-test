package uk.gov.hmcts.prd.internal

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object Internal_GETPbas {

  val config: Config = ConfigFactory.load()

  val GETPbas = 
  
    exec(http("RD11_Internal_RetrievesOrganisationsPaymentAccounts")
      .get("/refdata/internal/v1/organisations/pbas?email=${Email}")
      .header("Authorization", "Bearer ${accessToken}")
      .header("ServiceAuthorization", "Bearer ${s2sToken}")
      .header("UserEmail", "${Email}")
      .header("Content-Type", "application/json")
      .check(status is 200))
      
    .pause(Environment.thinkTime)
}