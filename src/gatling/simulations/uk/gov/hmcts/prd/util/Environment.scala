package uk.gov.hmcts.prd.util

import com.typesafe.config.ConfigFactory

object Environment {

  val environment: String = System.getProperty("env")
  val env = "perftest"
  val idamURL = "https://idam-web-public." + env + ".platform.hmcts.net"
  val IDAMUrl = "https://idam-api." + env + ".platform.hmcts.net"
  val S2SUrl = "http://rpe-service-auth-provider-" + env + ".service.core-compute-" + env + ".internal/testing-support"
  val BaseUrl = "http://rd-professional-api-" + env + ".service.core-compute-" + env + ".internal"
  val IDAM_Secret = ConfigFactory.load.getString("auth.clientSecret")
  val S2S_Secret = ConfigFactory.load.getString("aat_service.pass")
  val S2S_ServiceName = "rd_professional_api"

  val thinkTime = 7

  val minThinkTime = 5
  //10
  val maxThinkTime = 6
  //30

  val commonHeader = Map(
    "accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "sec-fetch-dest" -> "image",
    "sec-fetch-mode" -> "no-cors",
    "sec-fetch-site" -> "same-origin")
}
