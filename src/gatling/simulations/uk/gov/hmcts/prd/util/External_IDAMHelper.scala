package uk.gov.hmcts.prd.util

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object External_IDAMHelper {

 /* private val USERNAME = "testytesttest@test.net"
  private val PASSWORD = "4590fgvhbfgbDdffm3lk4j"*/
// private val USERNAME = "james@swansea.gov.uk"
//  private val PASSWORD = "Password123"
  private val USERNAME = "ccdloadtest1@gmail.com"
  private val PASSWORD = "Password12"
// private val USERNAME = "emshowcase@hmcts.net"
 // private val PASSWORD = "4590fgvhbfgbDdffm3lk4j"
  // below are for aat
/*private val USERNAME = "bundle-tester--518511189@gmail.com"
  private val PASSWORD = "4590fgvhbfgbDdffm3lk4j"*/


  val thinktime = Environment.thinkTime


  val getIdamTokenLatest=
    exec(http("Token_010_015_GetAuthToken")
         //.post(Env.getIdamUrl  + "/o/token?grant_type=password&username=${email}&password=Pass19word&client_id=rd-professional-api&client_secret=" + Env.getOAuthSecret + "&redirect_uri=https://rd-professional-api-perftest.service.core-compute-perftest.internal/oauth2redirect&scope=openid%20profile%20roles%20openid%20roles%20profile%20create-user%20manage-user")
         .post(Env.getIdamUrl  + "/o/token?grant_type=password&username=${CompanyEmail}&password=Testing1234&client_id=rd-professional-api&client_secret=" + Env.getOAuthSecret + "&redirect_uri=https://rd-professional-api-aat.service.core-compute-aat.internal/oauth2redirect&scope=openid%20profile%20roles%20openid%20roles%20profile%20create-user%20manage-user")
         .header("Content-Type", "application/x-www-form-urlencoded")
         .header("Content-Length", "0")
         .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("accessToken")))


}
