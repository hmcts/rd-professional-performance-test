package scenarios.internal

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils._

object Internal_GETOrgDetailsByUserId {

  val IdamIds = csv("IdamIds.csv").circular

  val GETOrgDetailsByUserId =

      feed(IdamIds)

      .exec(http("RD34_Internal_GETOrgDetailsByUserId")
        .get(Environment.BaseUrl + "/refdata/internal/v1/organisations/orgDetails/#{idamId}")
        .header("Authorization", "Bearer #{accessToken}")
        .header("ServiceAuthorization", "#{rd_professional_apiBearerToken}")
        .header("Content-Type", "application/json")
        .check(substring("organisationIdentifier")))

      .pause(Environment.thinkTime)

}