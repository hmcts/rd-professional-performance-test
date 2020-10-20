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
  private def companyEmail(): String = rng.alphanumeric.take(15).mkString
  private def paymentAccount1(): String = rng.alphanumeric.take(7).mkString
  private def paymentAccount2(): String = rng.alphanumeric.take(7).mkString
  private def addressLine1(): Int = rng.nextInt(999)

  val UpdateOrgMin = config.getString("internal.updateOrgMin").toInt

  val UpdateOrgMax = config.getString("internal.updateOrgMax").toInt

  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenInternal()

  val updateOrgString = "{\n   \"name\": \"Kapil${FirstName} Jain${LastName}\",\n    \"status\": \"ACTIVE\",\n    \"sraId\": \"TRA${SRAId}\",\n   \"sraRegulated\": true,\n   \"companyNumber\": \"${CompanyNumber}\",\n" +
    "\"companyUrl\": \"www.tr${CompanyURL}.com\",\n   \"superUser\": {\n       \"firstName\": \"${FirstName}\",\n       \"lastName\": \"${LastName}\",\n" +
    "\"email\": \"tpA${CompanyEmail}@email.co.uk\"\n,\n        \"jurisdictions\": [\n    {\n      \"id\": \"Divorce\"\n    },\n    {\n      \"id\": \"SSCS\"\n    },\n    {\n      \"id\": \"Probate\"\n    },\n    {\n      \"id\": \"Public Law\"\n    },\n    {\n      \"id\": \"Bulk Scanning\"\n    },\n    {\n      \"id\": \"Immigration & Asylum\"\n    },\n    {\n      \"id\": \"Civil Money Claims\"\n    },\n    {\n      \"id\": \"Employment\"\n    },\n    {\n      \"id\": \"Family public law and adoption\"\n    },\n    {\n      \"id\": \"Civil enforcement and possession\"\n    }\n  ]   },\n   \"paymentAccount\": [\n\n          \"PBA${PaymentAccount1}\",\"PBA${PaymentAccount2}\"\n\n   ],\n" +
    "\"contactInformation\": [\n       {\n           \"addressLine1\": \"${AddressLine1} high road\",\n           \"addressLine2\": \"${FirstName} ${LastName}\",\n           \"addressLine3\": \"Maharaj road\",\n" +
    "\"townCity\": \"West Kirby\",\n           \"county\": \"Wirral\",\n           \"country\": \"UK\",\n           \"postCode\": \"TEST1\",\n           \"dxAddress\": [\n" +
    "{\n                   \"dxNumber\": \"DX 1121111990\",\n                   \"dxExchange\": \"112111192099908492\"\n               }\n           ]\n       }\n   ]\n}"


  val OrgIdData = csv("prdIntOrgIDs.csv").circular

  val updateOrganisation = exec(_.setAll(
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

    .feed(OrgIdData)

    .exec(http("RD10_Internal_ActivateOrganization")
      .put("/refdata/internal/v1/organisations/${NewPendingOrg_Id}")
      .header("ServiceAuthorization", s2sToken)
      .header("Authorization", IdAMToken)
      .body(StringBody(updateOrgString))
      .header("Content-Type", "application/json")
      .check(status in (200,201)))
      
    .pause(UpdateOrgMin seconds, UpdateOrgMax seconds)

}
