package uk.gov.hmcts.prd.internal

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._
object Internal_GETAllOrganisation {

  val config: Config = ConfigFactory.load()

  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenInternal()

  val GetAllOrgMin = config.getString("internal.getAllOrgMin").toInt
  val GetAllOrgMax = config.getString("internal.getAllOrgMax").toInt

  val GETAllOrganisation = exec(http("RD03_Internal_GetAllOrganizations")
    .get("/refdata/internal/v1/organisations")
    .header("ServiceAuthorization", s2sToken)
    .header("Authorization", IdAMToken)
    .header("Content-Type", "application/json")
    .check(status is 200))
    .pause(GetAllOrgMin seconds, GetAllOrgMax seconds)
}
