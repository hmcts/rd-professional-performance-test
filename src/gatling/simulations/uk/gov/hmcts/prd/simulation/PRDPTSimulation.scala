package uk.gov.hmcts.prd.simulation

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.internal._
import uk.gov.hmcts.prd.external._
import scala.concurrent.duration._

class PRDPTSimulation extends Simulation{

  val config: Config = ConfigFactory.load()

  val IntPaceMin = config.getString("internal.intPaceMin").toInt

  val IntPaceMax = config.getString("internal.intPaceMax").toInt

  val ExtPaceMin = config.getString("external.intPaceMin").toInt

  val ExtPaceMax = config.getString("external.intPaceMax").toInt

  val Int_Ext_SCNPaceMin = config.getString("internal.Int_Ext_SCN_PaceMin").toInt

  val Int_Ext_SCNPaceMax = config.getString("internal.Int_Ext_SCN_PaceMax").toInt

  val Legacy_strategic_SCNPaceMin = config.getString("internal.Legacy_strategic_SCN_PaceMin").toInt

  val Legacy_strategic_SCNPaceMax = config.getString("internal.Legacy_strategic_SCN_PaceMax").toInt

  val httpProtocol = http
    .baseUrl(config.getString("baseUrl"))
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080))

 val Int_Scn = scenario("Professional Reference Data - Internal")
                  .exec(
                    Internal_CreateOrganisation.createOrganisation,
                    Internal_UpdateOrganisation.updateOrganisation,
                  Internal_GETOrganisationByID.GETOrganisationByID,
                  Internal_AddInternalUserToOrg.AddInternalUserToOrg,
                  Internal_GETInternalUserForGivenOrganisations.GETInternalUserForGivenOrganisations,
                )
                  .pause(IntPaceMin seconds, IntPaceMax seconds)


  setUp(
    Int_Scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
   // .assertions(forAll.responseTime.percentile3.lte(500))
   // .assertions(global.failedRequests.percent.lte(5))

}
