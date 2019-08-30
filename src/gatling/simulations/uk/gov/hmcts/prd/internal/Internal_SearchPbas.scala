package uk.gov.hmcts.prd.internal

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._

import scala.concurrent.duration._

object Internal_SearchPbas {

  val config: Config = ConfigFactory.load()

  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenInternal()

  val GetSearchPbasMin = config.getString("external.getSearchPbasMin").toInt

  val GetSearchPbasMax = config.getString("external.getSearchPbasMax").toInt

  val SearchPbas = exec(http("RD11_Internal_SearchPBAsByEmailAddress")
    .get("/search/pba/tpabsya3pt3o8w0qtg@email.co.uk")
    .header("ServiceAuthorization", s2sToken)
    .header("Authorization", IdAMToken)
    .header("Content-Type", "application/json")
    .check(status is 200))
    .pause(GetSearchPbasMin seconds, GetSearchPbasMax seconds)
}
