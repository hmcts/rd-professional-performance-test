package uk.gov.hmcts.prd.util

import com.warrenstrange.googleauth.GoogleAuthenticator
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util.Environment._

object  S2SHelper {

  // val getOTP =
  // exec(
  //   session => {
  //     val otp: String = String.valueOf(new GoogleAuthenticator().getTotpPassword(S2S_Secret))
  //     session.set("OTP", otp)
  //   })

  // val otpp="${OTP}"

  val S2SAuthToken =
    exec(http("Token_020_GetServiceToken")
      .post(S2SUrl + "/lease")
      .header("Content-Type", "application/json")
      .body(StringBody(
        s"""{
       "microservice": "${S2S_ServiceName}"
        }"""
      )).asJson
      .check(bodyString.saveAs("s2sToken"))
      // .check(bodyString.saveAs("responseBody"))
      )
    .pause(thinkTime)
      /*.exec( session => {
        println("the code of id is "+session("s2sToken").as[String])
        session
      })*/


  //microservice is a string defined in the Simulation and passed into the body below
  def s2s(microservice: String) = {

    exec(http("GetS2SToken")
      .post(S2SUrl + "/lease")
      .header("Content-Type", "application/json")
      .body(StringBody(s"""{"microservice":"${microservice}"}"""))
      .check(bodyString.saveAs(s"${microservice}BearerToken")))
      .exitHereIfFailed
  }

}