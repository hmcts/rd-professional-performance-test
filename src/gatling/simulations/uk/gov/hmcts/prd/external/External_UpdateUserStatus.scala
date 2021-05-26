package uk.gov.hmcts.prd.external

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import scala.concurrent.duration._

object External_UpdateUserStatus {

  val config: Config = ConfigFactory.load()

  val OrgIdData = csv("prdIntOrgIDs.csv").circular

  val UpdateUserStatusString = "{ \"idamStatus\" : \"SUSPENDED\"}"

  val EditUsrStatusMin = config.getString("internal.editUsrStatusMin").toInt

  val EditUsrStatusMax = config.getString("internal.editUsrStatusMax").toInt

  val UpdateInternalUserStatus =  repeat(1){

  feed(OrgIdData)
    .tryMax(2){
      exec(http("RD24_External_UpdateUserStatus")
          .put("/refdata/external/v1/organisations/users/${userId}?origin=EXUI")
        .header("Authorization", "Bearer ${accessToken}")
        .header("ServiceAuthorization", "Bearer ${s2sToken}")
        .body(StringBody(UpdateUserStatusString))
        .header("Content-Type", "application/json")
        .check(status is 200))
        }
      .pause(EditUsrStatusMin seconds, EditUsrStatusMax seconds)
  }
}
