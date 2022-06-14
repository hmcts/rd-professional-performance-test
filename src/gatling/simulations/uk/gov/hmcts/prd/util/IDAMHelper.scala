package uk.gov.hmcts.prd.util

import io.gatling.commons.util.TypeHelper.TypeValidator
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util.Environment._
import com.typesafe.config.{Config, ConfigFactory}

object IDAMHelper {

  val config: Config = ConfigFactory.load()

  val getIdamTokenLatest =
    
    exec(http("Token_010_GetAuthToken")
      // .post(IDAMUrl  + "/o/token?grant_type=password&username=vijay.idam.prd.user@hmcts.net&password=Pass19word&client_id=rd-professional-api&client_secret=" + IDAM_Secret + "&redirect_uri=https://rd-professional-api-${env}.service.core-compute-${env}.internal/oauth2redirect&scope=openid%20profile%20roles%20openid%20roles%20profile%20create-user%20manage-user")
      .post(IDAMUrl  + "/o/token?grant_type=password&username=prd-admin@gmail.com&password=Password12&client_id=rd-professional-api&client_secret=" + IDAM_Secret + "&redirect_uri=https://rd-professional-api-${env}.service.core-compute-${env}.internal/oauth2redirect&scope=openid%20profile%20roles%20openid%20roles%20profile%20create-user%20manage-user")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("accessToken")))

    .pause(Environment.thinkTime)

 val getIdamTokenLatest2 =

    exec(http("Token_030_GetAuthToken")
      .post(IDAMUrl  + "/o/token?grant_type=password&username=${Email}&password=P${Password}123&client_id=rd-professional-api&client_secret=" + IDAM_Secret + "&redirect_uri=https://rd-professional-api-${env}.service.core-compute-${env}.internal/oauth2redirect&scope=openid%20profile%20roles%20openid%20roles%20profile%20create-user%20manage-user")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("accessToken")))
      .exitHereIfFailed

    .pause(Environment.thinkTime)

  val getIdamTokenLatest3 =

    exec(http("Token_030_GetAuthToken")
      .post(IDAMUrl  + "/o/token?grant_type=password&username=prd-admin@gmail.com&password=Password12&client_id=rd_location_ref_api&client_secret=" + IDAM_Secret + "&redirect_uri=" + Environment.lrdUrl + "/oauth2redirect&scope=openid%20profile%20roles%20openid%20roles%20profile%20create-user%20manage-user")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("accessToken")))
      .exitHereIfFailed

    .pause(Environment.thinkTime)

}