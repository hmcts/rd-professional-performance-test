package uk.gov.hmcts.prd.internal
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._
object Internal_GETPbas {

  val config: Config = ConfigFactory.load()

  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenInternal()

  val GetPbasMin = config.getString("internal.getPbasMin").toInt

  val GetPbasMax = config.getString("internal.getPbasMax").toInt

  val GETPbas = exec(http("RD07_Internal_RetrievesOrganisationsPaymentAccounts")
    .get("/refdata/internal/v1/organisations/pbas?email=Kapil.uvyLo1pDRO0Qx5Z@gmail.com")
    .header("ServiceAuthorization", s2sToken)
    .header("Authorization", IdAMToken)
    .header("Content-Type", "application/json")
    .check(status is 200))
    .pause(GetPbasMin seconds, GetPbasMax seconds)
}
