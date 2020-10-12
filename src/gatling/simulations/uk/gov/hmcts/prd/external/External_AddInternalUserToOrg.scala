package uk.gov.hmcts.prd.external
import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import uk.gov.service.notify.{NotificationClient, NotificationList}

import scala.collection.JavaConverters.iterableAsScalaIterableConverter
import scala.concurrent.duration._
import scala.util.Random
import scala.util.matching.Regex
object External_AddInternalUserToOrg {
  private val rng: Random = new Random()
  private def internalUser_firstName(): String = rng.alphanumeric.take(20).mkString
  private def internalUser_lastName(): String = rng.alphanumeric.take(20).mkString
  private def internalUser_Email(): String = rng.alphanumeric.take(15).mkString
  val config: Config = ConfigFactory.load()
  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenExternal()

  val AddUsrMin = config.getString("external.addUsrMin").toInt

  val AddUsrMax = config.getString("external.addUsrMax").toInt

  val addInternalUserString = "{\n \"firstName\": \"Kapil ${InternalUser_FirstName}\",\n \"lastName\": \"Jain ${InternalUser_LastName}\",\n \"email\": \"Kapil.${InternalUser_Email}@gmail.com\",\n \"roles\": [\n   \"pui-user-manager\",\n   \"pui-organisation-manager\"\n ]\n,\n        \"jurisdictions\": [\n    {\n      \"id\": \"Divorce\"\n    },\n    {\n      \"id\": \"SSCS\"\n    },\n    {\n      \"id\": \"Probate\"\n    },\n    {\n      \"id\": \"Public Law\"\n    },\n    {\n      \"id\": \"Bulk Scanning\"\n    },\n    {\n      \"id\": \"Immigration & Asylum\"\n    },\n    {\n      \"id\": \"Civil Money Claims\"\n    },\n    {\n      \"id\": \"Employment\"\n    },\n    {\n      \"id\": \"Family public law and adoption\"\n    },\n    {\n      \"id\": \"Civil enforcement and possession\"\n    }\n  ]\n}"


  val AddInternalUserToOrg = repeat(1) {
    exec(_.setAll(
      ("InternalUser_FirstName", internalUser_firstName()),
      ("InternalUser_LastName", internalUser_lastName()),
      ("InternalUser_Email", internalUser_Email())
    ))

      .exec(http("RD14_External_AddInternalUserToOrganisation")
        .post("/refdata/external/v1/organisations/users/")
        .header("ServiceAuthorization", s2sToken)
        .header("Authorization", IdAMToken)
        .body(StringBody(addInternalUserString))
        .header("Content-Type", "application/json")
        .check(status is 201))
      .pause(AddUsrMin seconds, AddUsrMax seconds)

     .exec {
        session =>
          val client = new NotificationClient("sidam_perftest-b7ab8862-25b4-41c9-8311-cb78815f7d2d-ebb113ff-da17-4646-a39e-f93783a993f4")
          val pattern = new Regex("token.+")
          val str = findEmail(client, session("selfregemail").as[String])
          session.set("activationLink", (pattern findFirstMatchIn str.get).mkString)
      }
      .pause(120)
      .exec(http("SelfReg01_TX03_Password")
        .get("/users/register?&${activationLink}")
        .check(status.is(200))

        .check(css("input[name='token']", "value").saveAs("token"))
        .check(css("input[name='code']", "value").saveAs("code"))
        .check(css("input[name='_csrf']", "value").saveAs("_csrf")))
      .pause(150)
      .exec(http("SelfReg01_TX04_Activate").post("/users/activate")
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
      if (notification.getEmailAddress.get().equals(emailAddress)) {
        println("Found match for email " + emailAddress)
        return Some(notification.getBody)
      } else {
        println("Comparing " + notification.getEmailAddress.get() + " with " + emailAddress)
      }
    }
    None
  }

}
