package utils

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment._

object  S2SHelper {

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