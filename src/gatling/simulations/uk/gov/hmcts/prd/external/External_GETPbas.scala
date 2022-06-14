package uk.gov.hmcts.prd.external

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object External_GETPbas {

  val config: Config = ConfigFactory.load()

  // val GetPbasMin = config.getString("external.getPbasMin").toInt
  // val GetPbasMax = config.getString("external.getPbasMax").toInt

  val GETPbas = 

    repeat(2) {
    
      exec(http("RD24_External_RetrievesOrganisationsPaymentAccounts")
        .get("/refdata/external/v1/organisations/pbas?email=${Email}")
        .header("Authorization", "Bearer ${accessToken}")
        .header("ServiceAuthorization", "Bearer ${s2sToken}")
        .header("UserEmail", "${Email}")
        .header("Content-Type", "application/json")
        .check(jsonPath("$.organisationEntityResponse.paymentAccount[1]").saveAs("PBANumber2"))
        .check(jsonPath("$.organisationEntityResponse.paymentAccount[2]").saveAs("PBANumber3")))

      .pause(Environment.thinkTime)
    }
}