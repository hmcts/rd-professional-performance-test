package uk.gov.hmcts.prd.util

import com.typesafe.config.ConfigFactory
import com.warrenstrange.googleauth.GoogleAuthenticator
import io.restassured.RestAssured
import io.restassured.config.EncoderConfig
import io.restassured.http.ContentType
import io.restassured.parsing.Parser;
import java.util.Map;
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

package object PRDTokenGenerator {

  private val config = ConfigFactory.load()

  val TOKEN_LEASE_URL = config.getString("s2sUrl")
  val USERTOKEN_SidAM_URL = config.getString("idam_api_url")
  val clientsecret = config.getString("auth.clientSecret")
  val RD_URL = config.getString("baseUrl")

  def generateS2SToken() : String = {

     val authenticator: GoogleAuthenticator = new GoogleAuthenticator()

    //val password = authenticator.getTotpPassword(config.getString("aat_service.pass"))
    val password = config.getString("aat_service.pass")

    //System.out.println("password::" + password);

    val jsonPayload: String = """{"microservice":"""" + config.getString("aat_service.name") + """","oneTimePassword":"""" + password + """"}"""

    val s2sRequest = RestAssured.given
                    .contentType("application/json")
                    .accept("application/json")
                    .proxy("proxyout.reform.hmcts.net", 8080)
                    .body(jsonPayload)
                    .post(TOKEN_LEASE_URL +"/testing-support/lease")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response()

    val token = s2sRequest.asString()

    token

  }

  //=======================================

  def generateSIDAMUserTokenInternal() : String = {
    return generateSIDAMUserTokenInternal("mallikarjun.puttana@hmcts.net")
  }

  def generateSIDAMUserTokenInternal(userName : String) : String = {

  val authCodeRequest = RestAssured.given().config(RestAssured.config()
      .encoderConfig(EncoderConfig.encoderConfig()
        .encodeContentTypeAs("x-www-form-urlencoded",
          ContentType.URLENC)))
      .contentType("application/x-www-form-urlencoded; charset=UTF-8")
      .proxy("proxyout.reform.hmcts.net", 8080)
      .formParam("username", userName)
      .formParam("password", "Testing1234")
      .formParam("client_id", "rd-professional-api")
      .formParam("client_secret", clientsecret)
      .formParam("redirect_uri", RD_URL + "/oauth2redirect")
      .formParam("grant_type", "password")
       .formParam("scope", "openid profile roles create-user manage-user search-user")
      .request()


    System.out.println("clientSecret::" + clientsecret);

    val response = authCodeRequest.post(USERTOKEN_SidAM_URL + ":443/o/token")
    val statusCode = response.getStatusCode()
    val tokenStr = response.asString()
    val tokenIndexStart = tokenStr.indexOf(":")
    val tokenIndexEnd = tokenStr.indexOf(",")
    val token =  tokenStr.substring(tokenIndexStart+2,tokenIndexEnd -1 )

    "Bearer " + token
  }

  def generateSIDAMUserTokenExternal() : String = {
    return generateSIDAMUserTokenExternal("")
  }

  def generateSIDAMUserTokenExternal(userName : String) : String = {

  val authCodeRequest = RestAssured.given().config(RestAssured.config()
      .encoderConfig(EncoderConfig.encoderConfig()
        .encodeContentTypeAs("x-www-form-urlencoded",
          ContentType.URLENC)))
      .contentType("application/x-www-form-urlencoded; charset=UTF-8")
      .proxy("proxyout.reform.hmcts.net", 8080)
      .formParam("username", userName)
      .formParam("password", "Password12")
      .formParam("client_id", "rd-professional-api")
      .formParam("client_secret", clientsecret)
      .formParam("redirect_uri", RD_URL + "/oauth2redirect")
      .formParam("grant_type", "password")
      .formParam("scope", "openid profile roles create-user manage-user search-user")
      .request();


    val response = authCodeRequest.post(USERTOKEN_SidAM_URL + ":443/o/token")
    val statusCode = response.getStatusCode()
    val tokenStr = response.asString()
    val tokenIndexStart = tokenStr.indexOf(":")
    val tokenIndexEnd = tokenStr.indexOf(",")
    val token =  tokenStr.substring(tokenIndexStart+2,tokenIndexEnd -1 )

    "Bearer " + "Bearer" + token
  }

}