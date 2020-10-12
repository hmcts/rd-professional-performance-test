package uk.gov.hmcts.prd.external

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._

import scala.concurrent.duration._
import scala.util.Random
object External_CreateOrganisation {
  val s2sToken = PRDTokenGenerator.generateS2SToken()

  private val rng: Random = new Random()
  private def sRAId(): String = rng.alphanumeric.take(15).mkString
  private def companyNumber(): String = rng.alphanumeric.take(8).mkString
  private def companyURL(): String = rng.alphanumeric.take(15).mkString
  private def firstName(): String = rng.alphanumeric.take(20).mkString
  private def lastName(): String = rng.alphanumeric.take(20).mkString
  private def companyEmail(): String = "exui"+rng.alphanumeric.take(10).mkString + "@mailtest.gov.uk"
  private def paymentAccount1(): String = rng.alphanumeric.take(7).mkString
  private def paymentAccount2(): String = rng.alphanumeric.take(7).mkString
  private def addressLine1(): Int = rng.nextInt(999)

  val config: Config = ConfigFactory.load()

  val createExtOrgString = "{\n   \"name\": \"Kapil${FirstName} Jain${LastName}\",\n   \"sraId\": \"TRA${SRAId}\",\n   \"sraRegulated\": true,\n   \"companyNumber\": \"${CompanyNumber}\",\n" +
    "\"companyUrl\": \"${CompanyEmail}\",\n   \"superUser\": {\n       \"firstName\": \"${FirstName}\",\n       \"lastName\": \"${LastName}\",\n" +
    "\"email\": \"tpA${CompanyEmail}@mailinator.com\"\n,\n        \"jurisdictions\": [\n    {\n      \"id\": \"DIVORCE\"\n    },\n    {\n      \"id\": \"SSCS\"\n    },\n    {\n      \"id\": \"PROBATE\"\n    },\n    {\n      \"id\": \"PUBLICLAW\"\n    },\n    {\n      \"id\": \"BULK SCANNING\"\n    },\n    {\n      \"id\": \"IA\"\n    },\n    {\n      \"id\": \"CMC\"\n    },\n    {\n      \"id\": \"EMPLOYMENT\"\n    },\n    {\n      \"id\": \"Family public law and adoption\"\n    },\n    {\n      \"id\": \"Civil enforcement and possession\"\n    }\n  ]   },\n   \"paymentAccount\": [\n\n          \"PBA${PaymentAccount1}\",\"PBA${PaymentAccount2}\"\n\n   ],\n" +
    "\"contactInformation\": [\n       {\n           \"addressLine1\": \"${AddressLine1} high road\",\n           \"addressLine2\": \"${FirstName} ${LastName}\",\n           \"addressLine3\": \"Maharaj road\",\n" +
    "\"townCity\": \"West Kirby\",\n           \"county\": \"Wirral\",\n           \"country\": \"UK\",\n           \"postCode\": \"TEST1\",\n           \"dxAddress\": [\n" +
    "{\n                   \"dxNumber\": \"DX 1121111990\",\n                   \"dxExchange\": \"112111192099908492\"\n               }\n           ]\n       }\n   ]\n}"

  //tpA${CompanyEmail}@email.co.uk

  val CreateOrgMin = config.getString("external.createOrgMin").toInt

  val CreateOrgMax = config.getString("external.createOrgMax").toInt

  val createOrganisation = exec(_.setAll(
    ("SRAId", sRAId()),
    ("CompanyNumber", companyNumber()),
    ("CompanyURL", companyURL()),
    ("FirstName",firstName()),
    ("LastName",lastName()),
    ("CompanyEmail",companyEmail()),
    ("PaymentAccount1",paymentAccount1()),
    ("PaymentAccount2",paymentAccount2()),
    ("AddressLine1",addressLine1())
  ))

    .exec(http("RD01_External_CreateOrganization")
      .post("/refdata/external/v1/organisations")
      .header("ServiceAuthorization", s2sToken)
      .body(StringBody(createExtOrgString))
      .header("Content-Type", "application/json")
      .check(jsonPath("$.organisationIdentifier").saveAs("NewPendingOrg_Id"))
      .check(status in (200,201)))
    .pause(CreateOrgMin seconds, CreateOrgMax seconds)

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
