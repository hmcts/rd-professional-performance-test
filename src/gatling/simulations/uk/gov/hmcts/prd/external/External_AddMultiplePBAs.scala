package uk.gov.hmcts.prd.external

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._

import scala.concurrent.duration._
import scala.util.Random

object External_AddMultiplePBAs {

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

  val AddMultiplePbas = 
  
    exec(http("RD25_External_AddMultiplePBAs")
      .post("/refdata/external/v1/organisations/pba")
      .header("Authorization", "Bearer ${accessToken}")
      .header("ServiceAuthorization", "Bearer ${s2sToken}")
      .header("Content-Type", "application/json")
      .body(StringBody(editPbasString)))

    .pause(Environment.thinkTime)
}
