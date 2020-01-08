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

  val editPbasString = "{ \"paymentAccounts\": [ \"PBA0000004\",\"PBA0000005\" ]}"


  val EditPbasMin = config.getString("internal.editPbasMin").toInt

  val EditPbasMax = config.getString("internal.editPbasMax").toInt

  val EditPbas = exec(http("R3_Internal_EditPBA")
    .put("/refdata/internal/v1/organisations/J3666GW/pbas")
    .header("ServiceAuthorization", s2sToken)
    .header("Authorization", IdAMToken)
    .header("Content-Type", "application/json")
    .body(StringBody(editPbasString))
    .check(status is 200))
    .pause(EditPbasMin seconds, EditPbasMax seconds)
}
