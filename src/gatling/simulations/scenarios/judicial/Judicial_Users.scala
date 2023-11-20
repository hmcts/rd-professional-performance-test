package scenarios.judicial

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils._
import scala.util.Random
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object Judicial_Users {

  val JudicialPostUsers = 

    group("RD_Judicial_Users") {
      exec(http("RD_Judicial_Users_#{service}")
        .post(Environment.judicialUrl + "/refdata/judicial/users/")
        .header("Authorization", "Bearer #{accessToken}")
        .header("ServiceAuthorization", "Bearer #{rd_judicial_apiBearerToken}")
        .header("Content-Type", "application/json")
        // .header("Accept", "application/vnd.jrd.api+json;Version=2.0")
        .body(ElFileBody("bodies/judicial/PostUsers.json")))
    }

    .pause(Environment.thinkTime)

  val JudicialPostUsersSearch = 

    exec(http("RD_Judicial_UsersSearch")
      .post(Environment.judicialUrl + "/refdata/judicial/users/search")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "Bearer #{rd_judicial_apiBearerToken}")
      .header("Content-Type", "application/json")
      // .header("Accept", "application/vnd.jrd.api+json;Version=2.0")
      .body(ElFileBody("bodies/judicial/PostUsersSearch.json")))

    .pause(Environment.thinkTime)
}