package uk.gov.hmcts.prd.internal

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import scala.util.Random
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object Internal_UpdateOrganisation {

  val config: Config = ConfigFactory.load()

  private val rng: Random = new Random()
  private def sRAId(): String = rng.alphanumeric.take(15).mkString
  private def companyNumber(): String = rng.alphanumeric.take(8).mkString
  private def companyURL(): String = rng.alphanumeric.take(15).mkString
  private def firstName(): String = rng.alphanumeric.take(20).mkString
  private def lastName(): String = rng.alphanumeric.take(20).mkString
  private def paymentAccount1(): String = rng.alphanumeric.take(7).mkString
  private def paymentAccount2(): String = rng.alphanumeric.take(7).mkString
  private def addressLine1(): Int = rng.nextInt(999)

  val OrgIdData = csv("prdIntOrgIDs.csv").circular

  val updateOrganisation = 
  
    exec(_.setAll(
      ("SRAId", sRAId()),
      ("CompanyNumber", companyNumber()),
      ("CompanyURL", companyURL()),
      ("PaymentAccount1",paymentAccount1()),
      ("PaymentAccount2",paymentAccount2()),
      ("AddressLine1",addressLine1())
    ))

    .feed(OrgIdData)

    .exec(http("RD03_Internal_PendingOrganization")
      .put("/refdata/internal/v1/organisations/${NewPendingOrg_Id}")
      .header("Authorization", "Bearer ${accessToken}")
      .header("ServiceAuthorization", "Bearer ${s2sToken}")
      // .body(StringBody(updateOrgString))
      .body(ElFileBody("bodies/internal/InternalUpdateOrganisationPENDING.json"))
      .header("Content-Type", "application/json"))

    .pause(Environment.thinkTime)

    .exec(http("RD04_Internal_ActivateOrganization")
      .put("/refdata/internal/v1/organisations/${NewPendingOrg_Id}")
      .header("Authorization", "Bearer ${accessToken}")
      .header("ServiceAuthorization", "Bearer ${s2sToken}")
      // .body(StringBody(updateOrgString))
      .body(ElFileBody("bodies/internal/InternalUpdateOrganisationACTIVE.json"))
      .header("Content-Type", "application/json"))

    .pause(Environment.thinkTime)
}