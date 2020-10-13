package uk.gov.hmcts.prd.internal

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._

object DeleteOrganisation {

  val config: Config = ConfigFactory.load()


  val DeleteOrganisation = exec(http("RD03_Internal_DeleteOrganizations")
    .delete("http://rd-professional-api-perftest.service.core-compute-perftest.internal/refdata/internal/v1/organisations/MK8OK0D")
    .header("Authorization", "Bearer${accessToken}")
    .header("ServiceAuthorization", "Bearer${s2sToken}")
    .header("Content-Type", "application/json")
    .check(status is 200))
    .pause(10)
}
