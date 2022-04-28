package uk.gov.hmcts.prd.internal

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._

import scala.concurrent.duration._
import scala.util.Random

object Internal_EditPbas {

  val config: Config = ConfigFactory.load()
  private val rng: Random = new Random()
  private def paymentAccount1(): String = rng.alphanumeric.take(7).mkString
  private def paymentAccount2(): String = rng.alphanumeric.take(7).mkString

  val editPbasString = "{ \"paymentAccounts\": [ \"PBA${PaymentAccount1}\",\"PBA${PaymentAccount2}\" ]}"

  val createAccounts = 
  
    exec(_.setAll(
      ("PaymentAccount1",paymentAccount1()),
      ("PaymentAccount2",paymentAccount2())
    ))

  val EditPbas = 
  
    exec(http("RD14_Internal_EditPBA")
      .put("/refdata/internal/v1/organisations/${NewPendingOrg_Id}/pbas")
      .header("Authorization", "Bearer ${accessToken}")
      .header("ServiceAuthorization", "Bearer ${s2sToken}")
      .header("Content-Type", "application/json")
      .body(StringBody(editPbasString))
      .check(status is 200))

    .pause(Environment.thinkTime)
}
