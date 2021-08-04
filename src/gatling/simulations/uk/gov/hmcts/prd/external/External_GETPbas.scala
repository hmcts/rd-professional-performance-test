package uk.gov.hmcts.prd.external

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object External_GETPbas {

  val config: Config = ConfigFactory.load()

  val GetPbasMin = config.getString("external.getPbasMin").toInt

  val GetPbasMax = config.getString("external.getPbasMax").toInt

  val GETPbas = exec(http("RD20_External_RetrievesOrganisationsPaymentAccounts")
   .get("/refdata/external/v1/organisations/pbas")
    .header("Authorization", "Bearer ${accessToken}")
    .header("ServiceAuthorization", "Bearer ${s2sToken}")
    .header("email", "${Email}")
    .header("Content-Type", "application/json")
    .check(status is 200))
    .pause(GetPbasMin seconds, GetPbasMax seconds)
}
