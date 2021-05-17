package uk.gov.hmcts.prd.external

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import scala.concurrent.duration._

object External_SearchPbas {

  val config: Config = ConfigFactory.load()

  val GetSearchPbasMin = config.getString("external.getSearchPbasMin").toInt

  val GetSearchPbasMax = config.getString("external.getSearchPbasMax").toInt

  val SearchPbas = repeat(1){
    exec(http("RD17_External_SearchPBAsByEmailAddress")
      .get("/search/pba/kapil.jain@hmcts.net")
      .header("Authorization", "Bearer ${accessToken}")
      .header("ServiceAuthorization", "Bearer ${s2sToken}")
      .header("Content-Type", "application/json")
      .check(status is 200))
      .pause(GetSearchPbasMin seconds, GetSearchPbasMax seconds)
  }
}
