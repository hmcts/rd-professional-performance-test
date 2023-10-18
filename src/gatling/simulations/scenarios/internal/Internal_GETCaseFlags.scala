package scenarios.internal

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils._

object Internal_GETCaseFlags {

  val flagServices = csv("FlagServices.csv").random
  val flagType = Array(
    Map("flagType" -> "CASE"),
    Map("flagType" -> "PARTY")).random

  val GetCaseFlags = {

    repeat(15) {

      feed(flagServices)

      //XUI (the only current consumer of case/party flags) always calls this location API first to retrieve the service code for the case type
      .exec(S2SHelper.s2s("rd_location_ref_api"))

      .exec(http("RD32_Internal_GetServiceCode")
        .get(Environment.lrdUrl + "/refdata/location/orgServices?ccdCaseType=#{ccdCaseType}")
        .header("Authorization", "Bearer #{accessToken}")
        .header("ServiceAuthorization", "#{rd_location_ref_apiBearerToken}")
        .header("Content-Type", "application/json")
        .check(jsonPath("$[0].service_code").saveAs("serviceCode")))

      .feed(flagType)

      .exec(S2SHelper.s2s("rd_commondata_api"))

      .exec(http("RD33_Internal_GetFlags#{flagType}#{ccdCaseType}")
        .get(Environment.commonDataUrl + "/refdata/commondata/caseflags/service-id=#{serviceCode}?flag-type=#{flagType}")
        .header("Authorization", "Bearer #{accessToken}")
        .header("ServiceAuthorization", "#{rd_commondata_apiBearerToken}")
        .header("Content-Type", "application/json")
        .check(substring("flags")))

      .pause(Environment.thinkTime)

    }
  }
}