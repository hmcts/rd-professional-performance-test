package uk.gov.hmcts.prd.external

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._

import scala.concurrent.duration._
import scala.util.Random
object External_CreateOrganisation {

  private val rng: Random = new Random()
  private def sRAId(): String = rng.alphanumeric.take(15).mkString
  private def companyNumber(): String = rng.alphanumeric.take(8).mkString
  private def companyURL(): String = rng.alphanumeric.take(15).mkString
  private def paymentAccount1(): String = rng.alphanumeric.take(7).mkString
  private def paymentAccount2(): String = rng.alphanumeric.take(7).mkString
  private def paymentAccount3(): String = rng.alphanumeric.take(7).mkString
  private def addressLine1(): Int = rng.nextInt(999)

  val config: Config = ConfigFactory.load()

  val createOrganisation = exec(_.setAll(
    ("SRAId", sRAId()),
    ("CompanyNumber", companyNumber()),
    ("CompanyURL", companyURL()),
    ("PaymentAccount1",paymentAccount1()),
    ("PaymentAccount2",paymentAccount2()),
    ("PaymentAccount3",paymentAccount3()),
    ("AddressLine1",addressLine1())
  ))

    .exec(http("RD19_External_CreateOrganization")
      .post("/refdata/external/v1/organisations")
      .header("ServiceAuthorization", "Bearer ${s2sToken}")
      .body(ElFileBody("bodies/external/CreateExternalOrg.json"))
      .header("Content-Type", "application/json")
      .check(jsonPath("$.organisationIdentifier").saveAs("NewPendingOrg_Id"))
      .check(status in (200,201)))

    .pause(Environment.thinkTime)

/*.exec {
      session =>
        val fw = new BufferedWriter(new FileWriter("OrgId.csv", true))
        try {
          fw.write(session("NewPendingOrg_Id").as[String]  +"\r\n")
        }
        finally fw.close()
        session
    }*/

}