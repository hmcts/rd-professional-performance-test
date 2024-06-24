package scenarios.caseworker

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils._

object CW_Post {

  val services = csv("Services.csv").random

  val PostScenario = 
  
    exec(http(requestName="CRD_010_UploadFile1")
      .post(Environment.caseworkerUrl + "/refdata/case-worker/upload-file")
      .header("Authorization", "Bearer #{accessToken}")
      .header("serviceAuthorization", "Bearer #{rd_caseworker_ref_apiBearerToken}")
      .formUpload("file", "bodies/crd/Staff Data Upload Template.xlsx")
      .check(status.is(200)))

    .pause(Environment.thinkTime)

    .exec(http(requestName="CRD_020_UploadFile2")
      .post(Environment.caseworkerUrl + "/refdata/case-worker/upload-file")
      .header("Authorization", "Bearer #{accessToken}")
      .header("serviceAuthorization", "Bearer #{rd_caseworker_ref_apiBearerToken}")
      .formUpload("file", "bodies/crd/Service to IDAM Role Mapping-2.xlsx")
      .check(status.is(200)))

    .pause(Environment.thinkTime)

  val FetchUsersById =

    exec(http(requestName="CRD_030_FetchUsers")
      .post(Environment.caseworkerUrl + "/refdata/case-worker/users/fetchUsersById")
      .header("Authorization", "Bearer #{accessToken}")
      .header("serviceAuthorization", "Bearer #{rd_caseworker_ref_apiBearerToken}")
      .header("Content-Type", "application/json")
      .body(ElFileBody("bodies/crd/user_ids.json"))
      .check(status.is(200)))

    .pause(Environment.thinkTime)

  val FetchStaffUsersByService = 

    repeat(5) {

      feed(services)

      .exec(http(requestName="CRD_040_FetchStaffUsersByService")
        .get(Environment.caseworkerUrl + "/refdata/internal/staff/usersByServiceName?ccd_service_names=#{service}&page_size=12000")
        .header("Authorization", "Bearer #{accessToken}")
        .header("serviceAuthorization", "Bearer #{rd_caseworker_ref_apiBearerToken}")
        .header("Content-Type", "application/json")
        .check(status.is(200)))

      .pause(Environment.thinkTime)
    }
}