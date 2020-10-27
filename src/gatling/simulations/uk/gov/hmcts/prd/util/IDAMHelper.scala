package uk.gov.hmcts.prd.util

import io.gatling.commons.util.TypeHelper.TypeValidator
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util.Environment._

object IDAMHelper {

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

 val environment =
  exec{
   session =>
    println("This is the environment: " + env)
    session
  }
    .pause(thinkTime)

 val getIdamTokenLatest=
  if (env == "perftest"){
    exec(http("Token_010_GetAuthToken")
         .post(IDAMUrl  + "/o/token?grant_type=password&username=vijay.idam.prd.user@hmcts.net&password=Pass19word&client_id=rd-professional-api&client_secret=" + Env.getOAuthSecret + "&redirect_uri=https://rd-professional-api-" + env + ".service.core-compute-" + env + ".internal/oauth2redirect&scope=openid%20profile%20roles%20openid%20roles%20profile%20create-user%20manage-user")
         .header("Content-Type", "application/x-www-form-urlencoded")
         .header("Content-Length", "0")
         .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("accessToken")))}

  else if (env == "aat"){
  exec(http("Token_010_GetAuthToken")
    .post(IDAMUrl  + "/o/token?grant_type=password&username=mallikarjun.puttana@hmcts.net&password=Testing1234&client_id=rd-professional-api&client_secret=" + Env.getOAuthSecret + "&redirect_uri=https://rd-professional-api-" + env + ".service.core-compute-" + env + ".internal/oauth2redirect&scope=openid%20profile%20roles%20openid%20roles%20profile%20create-user%20manage-user")
    .header("Content-Type", "application/x-www-form-urlencoded")
    .header("Content-Length", "0")
    .check(status is 200)
    .check(jsonPath("$.access_token").saveAs("accessToken")))}

  else {
   exec{
   session =>
   println("Please enter a valid environment")
    session}
  }
    .pause(thinkTime)

 val getIdamTokenLatest2=
  exec(http("Token_030_GetAuthToken")
    .post(IDAMUrl  + "/o/token?grant_type=password&username=${Email}&password=${Password}&client_id=rd-professional-api&client_secret=" + Env.getOAuthSecret + "&redirect_uri=https://rd-professional-api-" + env + ".service.core-compute-" + env + ".internal/oauth2redirect&scope=openid%20profile%20roles%20openid%20roles%20profile%20create-user%20manage-user")
    .header("Content-Type", "application/x-www-form-urlencoded")
    .header("Content-Length", "0")
    .check(status is 200)
    .check(jsonPath("$.access_token").saveAs("accessToken")))
    .pause(thinkTime)

}
