package utils

// import io.gatling.commons.util.TypeHelper.TypeValidator
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment._
import com.typesafe.config.{Config, ConfigFactory}

object IDAMHelper {

  val config: Config = ConfigFactory.load()

  val getIdamTokenLatest =
    
    exec(http("GetIdamToken")
      .post(IDAMUrl  + "/o/token?grant_type=password&username=prd-admin@gmail.com&password=Password12&client_id=rd-professional-api&client_secret=" + IDAM_Secret + "&redirect_uri=https://rd-professional-api-#{env}.service.core-compute-#{env}.internal/oauth2redirect&scope=openid%20profile%20roles%20openid%20roles%20profile%20create-user%20manage-user")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("accessToken")))

    .pause(Environment.thinkTime)

  val getAdminIdamToken = 

    exec(http("GetIdamToken")
      .post(IDAMUrl  + "/o/token?grant_type=password&username=#{adminEmail}&password=Password12&client_id=rd-professional-api&client_secret=" + IDAM_Secret + "&redirect_uri=https://rd-professional-api-#{env}.service.core-compute-#{env}.internal/oauth2redirect&scope=openid%20profile%20roles%20openid%20roles%20profile%20create-user%20manage-user")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(jsonPath("$.access_token").saveAs("accessToken")))

    .pause(Environment.thinkTime)

  val getCrdIdamToken = 

    exec(http("GetIdamToken")
      .post(IDAMUrl + "/o/token?grant_type=password&username=CWR-func-test-user-test500@justice.gov.uk&password=Testing1234&client_id=rd-caseworker-ref-api&client_secret=" + IDAM_Secret + "&redirect_uri=https://rd-caseworker-ref-api-#{env}.service.core-compute-#{env}.internal/oauth2redirect&scope=openid%20profile%20roles%20manage-user%20create-user%20search-user")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(jsonPath("$.access_token").saveAs("accessToken")))

    .pause(Environment.thinkTime)

  val getJrdIdamToken = 

    exec(http("GetIdamToken")
      .post(IDAMUrl + "/o/token?grant_type=password&username=JudicialPerftest001@justice.gov.uk&password=Password12&client_id=rd-caseworker-ref-api&client_secret=" + IDAM_Secret + "&redirect_uri=https://rd-judicial-api-#{env}.service.core-compute-#{env}.internal/oauth2redirect&scope=openid%20profile%20roles%20manage-user%20create-user%20search-user")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(jsonPath("$.access_token").saveAs("accessToken")))

    .pause(Environment.thinkTime)

}