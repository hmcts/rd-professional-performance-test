package scenarios.internal

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils._
import scala.util.Random
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object Internal_CreateOrganisation {

  val config: Config = ConfigFactory.load()

  private val rng: Random = new Random()
  private def sRAId(): String = rng.alphanumeric.take(15).mkString
  private def companyNumber(): String = rng.alphanumeric.take(8).mkString
  private def companyURL(): String = rng.alphanumeric.take(15).mkString
  private def paymentAccount1(): String = rng.alphanumeric.take(7).mkString
  private def paymentAccount2(): String = rng.alphanumeric.take(7).mkString
  private def addressLine1(): Int = rng.nextInt(999)

  val createOrganisation = 
  
    exec(_.setAll(
      ("SRAId", sRAId()),
      ("CompanyNumber", companyNumber()),
      ("CompanyURL", companyURL()),
      ("PaymentAccount1",paymentAccount1()),
      ("PaymentAccount2",paymentAccount2()),
      ("AddressLine1",addressLine1())
    ))

    .exec(http("RD01_Internal_CreateOrganization")
      .post("/refdata/internal/v1/organisations")
      .header("ServiceAuthorization", "Bearer #{s2sToken}")
      .body(ElFileBody("bodies/internal/CreateInternalOrg.json"))
      .header("Content-Type", "application/json")
      .check(jsonPath("$.organisationIdentifier").saveAs("NewPendingOrg_Id"))
      .check(status in (200,201)))

    .pause(Environment.thinkTime)

  val createOtherOrganisation = 

    exec(_.setAll(
      ("SRAId", sRAId()),
      ("CompanyNumber", companyNumber()),
      ("CompanyURL", companyURL()),
      ("PaymentAccount1",paymentAccount1()),
      ("PaymentAccount2",paymentAccount2()),
      ("AddressLine1",addressLine1())
    ))

    .exec(http("RD01_Internal_CreateOtherOrganization")
      .post("/refdata/internal/v1/organisations")
      .header("ServiceAuthorization", "Bearer #{s2sToken}")
      .body(ElFileBody("bodies/internal/CreateOtherInternalOrg.json"))
      .header("Content-Type", "application/json")
      .check(jsonPath("$.organisationIdentifier").saveAs("NewPendingOrg_Id"))
      .check(status in (200,201)))

    .pause(Environment.thinkTime)
}
