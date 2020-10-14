package uk.gov.hmcts.prd.internal
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._
object Internal_GETOrganisationsByStatusPENDING {

  val config: Config = ConfigFactory.load()

  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenInternal()

  val GetPendingOrgMin = config.getString("internal.getPendingOrgMin").toInt

  val GetPendingOrgMax = config.getString("internal.getPendingOrgMax").toInt

  val GETOrganisationsByStatusPENDING = exec(http("RD06_Internal_GetOrganizationsByStatusPENDING")
    .get("/refdata/internal/v1/organisations?status=PENDING")
    .header("Authorization", "Bearer ${accessToken}")
    .header("ServiceAuthorization", "Bearer ${s2sToken}")
    .header("Content-Type", "application/json")
    .check(status is 200))
    .pause(GetPendingOrgMin seconds, GetPendingOrgMax seconds)
}
