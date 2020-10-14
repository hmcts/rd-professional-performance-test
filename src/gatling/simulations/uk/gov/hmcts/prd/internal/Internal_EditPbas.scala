package uk.gov.hmcts.prd.internal

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._

import scala.concurrent.duration._

object Internal_EditPbas {

  val config: Config = ConfigFactory.load()

  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenInternal()

  val editPbasString = "{ \"paymentAccounts\": [ \"PBA0000014\",\"PBA0000015\" ]}"


  val EditPbasMin = config.getString("internal.editPbasMin").toInt

  val EditPbasMax = config.getString("internal.editPbasMax").toInt

  val EditPbas = exec(http("R3_Internal_EditPBA")
    .put("/refdata/internal/v1/organisations/${NewPendingOrg_Id}/pbas")
    .header("Authorization", "Bearer ${accessToken}")
    .header("ServiceAuthorization", "Bearer ${s2sToken}")
    .header("Content-Type", "application/json")
    .body(StringBody(editPbasString))
    .check(status is 200))
    .pause(EditPbasMin seconds, EditPbasMax seconds)
}
