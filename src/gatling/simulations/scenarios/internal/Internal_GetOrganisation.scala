package scenarios.internal

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object Internal_GetOrganisation {

  val config: Config = ConfigFactory.load()

  val GETOrg = 

    repeat(10) {
      exec(http("RD15_Internal_RetrievesOrganisationsPaymentAccounts")
        .get("/refdata/internal/v1/organisations?id=#{NewPendingOrg_Id}")
        .header("Authorization", "Bearer #{accessToken}")
        .header("ServiceAuthorization", "Bearer #{s2sToken}")
        .header("Content-Type", "application/json")
        .check(jsonPath("$.paymentAccount[0]").saveAs("PBANumber1"))
        .check(jsonPath("$.paymentAccount[1]").saveAs("PBANumber2")))
        
      .pause(2)
    }
}