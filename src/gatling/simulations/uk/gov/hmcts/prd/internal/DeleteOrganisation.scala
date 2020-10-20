package uk.gov.hmcts.prd.internal

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._

object DeleteOrganisation {

  val config: Config = ConfigFactory.load()
  val s2sToken = PRDTokenGenerator.generateS2SToken()
  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenInternal()

  val DeleteOrganisation = exec(http("RD03_Internal_DeleteOrganizations")
    .delete("/refdata/internal/v1/organisations/${NewPendingOrg_Id}")
    .header("ServiceAuthorization", s2sToken)
    .header("Authorization", IdAMToken)
    .header("Content-Type", "application/json")
    .check(status is 200))
}