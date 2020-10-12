package uk.gov.hmcts.prd.internal

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import uk.gov.service.notify.{NotificationClient, NotificationList}

import scala.collection.JavaConverters.iterableAsScalaIterableConverter
import scala.util.Random
import scala.util.matching.Regex
object Internal_AddInternalUserToOrg {

  private val rng: Random = new Random()
  private def internalUser_firstName(): String = rng.alphanumeric.take(20).mkString
  private def internalUser_lastName(): String = rng.alphanumeric.take(20).mkString
  private def internalUser_Email(): String = "exui"+rng.alphanumeric.take(10).mkString + "@mailtest.gov.uk";

  val config: Config = ConfigFactory.load()

  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenInternal()

  val OrgIdData = csv("prdIntOrgIDs.csv").circular
  val emailIdData = csv("OrgId.csv").circular

  val addInternalUserString = "{\n \"firstName\": \"${InternalUser_FirstName}\",\n \"lastName\": \"${InternalUser_LastName}\",\n \"email\": \"${InternalUser_Email}\",\n \"roles\": [\n   \"pui-user-manager\",\n   \"pui-case-manager\",\n   \"caseworker\",\n   \"caseworker-probate\",\n   \"caseworker-probate-solicitor\",\n   \"pui-organisation-manager\"\n ]\n,\n        \"jurisdictions\": [\n    {\n      \"id\": \"Divorce\"\n    },\n    {\n      \"id\": \"SSCS\"\n    },\n    {\n      \"id\": \"Probate\"\n    },\n    {\n      \"id\": \"Public Law\"\n    },\n    {\n      \"id\": \"Bulk Scanning\"\n    },\n    {\n      \"id\": \"Immigration & Asylum\"\n    },\n    {\n      \"id\": \"Civil Money Claims\"\n    },\n    {\n      \"id\": \"Employment\"\n    },\n    {\n      \"id\": \"Family public law and adoption\"\n    },\n    {\n      \"id\": \"Civil enforcement and possession\"\n    }\n  ]\n}"

  val AddIntUsrMin = config.getString("internal.addIntUsrMin").toInt

  val AddIntUsrMax = config.getString("internal.addIntUsrMax").toInt

  val AddInternalUserToOrg =  repeat(1){
    feed(emailIdData)

      /*exec(_.setAll(
          ("InternalUser_FirstName",internalUser_firstName()),
          ("InternalUser_LastName",internalUser_lastName()),
          ("InternalUser_Email",internalUser_Email())
        ))

      .feed(OrgIdData)

      .exec(http("RD08_Internal_AddInternalUserToOrganisation")
        .post("/refdata/internal/v1/organisations/${PRD_Org_ID}/users/")
        .header("ServiceAuthorization", s2sToken)
        .header("Authorization", IdAMToken)
        .body(StringBody(addInternalUserString))
        .header("Content-Type", "application/json")
        .check(status is 201))
      .pause(30)*/
    //below code is for email activation

          .exec {

        session =>
          val client = new NotificationClient("sidam_perftest-b7ab8862-25b4-41c9-8311-cb78815f7d2d-ebb113ff-da17-4646-a39e-f93783a993f4")
          val pattern = new Regex("token.+")
          val str = findEmail(client,session("emailId").as[String])
       // val str = findEmail(client,"exuitc4fp2@mailtest.gov.uk")
          session.set("activationLink", (pattern findFirstMatchIn str.get).mkString)
      }
        .pause(60)
        .exec(http("SelfReg01_TX03_Password")
          .get("https://idam-web-public.perftest.platform.hmcts.net/users/register?&${activationLink}")
          .check(status.is(200))

          .check(css("input[name='token']", "value").saveAs("token"))
          .check(css("input[name='code']", "value").saveAs("code"))
          .check(css("input[name='_csrf']", "value").saveAs("_csrf")))
        .pause(60)
        .exec(http("SelfReg01_TX04_Activate").post("https://idam-web-public.perftest.platform.hmcts.net/users/activate")
          .formParam("_csrf", "${_csrf}")
          .formParam("code", "${code}")
          .formParam("token", "${token}")
          .formParam("password1", "Pass19word")
          .formParam("password2", "Pass19word")
          .check(status.is(200)))
  }

  def findEmail(client: NotificationClient, emailAddress:String) : Option[String] = {
    var emailBody = findEmailByStatus(client, emailAddress, "created")
    if (emailBody.isDefined) {
      return emailBody
    }
    emailBody = findEmailByStatus(client, emailAddress, "sending")
    if (emailBody.isDefined) {
      return emailBody
    }
    emailBody = findEmailByStatus(client, emailAddress, "delivered")
    if (emailBody.isDefined) {
      return emailBody
    }
    findEmailByStatus(client, emailAddress, "failed")
  }

  def findEmailByStatus(client: NotificationClient, emailAddress: String, status: String) : Option[String] = {
    val notificationList = client.getNotifications(status, "email", null, null)
    println("Searching notifications from " + status)
    val emailBody = getEmailBodyByEmailAddress(notificationList, emailAddress)
    if (emailBody.isDefined) {
      return emailBody
    }
    None
  }

  def getEmailBodyByEmailAddress(notifications: NotificationList, emailAddress: String) : Option[String] = {
    for(notification <- notifications.getNotifications.asScala) {
      if (notification.getEmailAddress.get().equalsIgnoreCase(emailAddress)) {
        println("Found match for email " + emailAddress)
        return Some(notification.getBody)
      } else {
        println("Comparing " + notification.getEmailAddress.get() + " with " + emailAddress)
      }
    }
    None
  }


}
