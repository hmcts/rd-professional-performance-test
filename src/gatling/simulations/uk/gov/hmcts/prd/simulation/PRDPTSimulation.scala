package uk.gov.hmcts.prd.simulation

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.external._
import uk.gov.hmcts.prd.internal._
import uk.gov.hmcts.prd.util.{IDAMHelper, S2SHelper}

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
    .baseUrl("http://rd-professional-api-perftest.service.core-compute-perftest.internal")
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080))

 val Int_Scn = scenario("Professional Reference Data - Internal")
                  .exec(
                  //Internal_GETOrganisationByID.GETOrganisationByID,
                  Internal_AddInternalUserToOrg.AddInternalUserToOrg
                  //  External_GETInternalUserForActiveOrganisationByEmail.GETInternalUserForActiveOrganisationByEmail
                 // Internal_GETInternalUserForGivenOrganisations.GETInternalUserForGivenOrganisations,
                   // Internal_GETInternalUserForActiveOrganisationByEmail.GETInternalUserForActiveOrganisationByEmail
                )
                  .pause(IntPaceMin seconds, IntPaceMax seconds)


  val Ext_Scn = scenario("Professional Reference Data - External")
                .exec(
                 External_CreateOrganisation.createOrganisation,
                  Internal_GETOrganisationsByStatusPENDING.GETOrganisationsByStatusPENDING,
                  External_UpdateOrganisation.updateOrganisation,
                  External_GETOrganisationByID.GETOrganisationByID,
                  External_AddInternalUserToOrg.AddInternalUserToOrg
                )
               .pause(ExtPaceMin seconds, ExtPaceMax seconds)


  val Int_Ext_SCN = scenario("Professional Reference Data - Internal + External")
                    .exec(External_GETInternalUserForActiveOrganisationByEmail.GETInternalUserForActiveOrganisationByEmail, Internal_GETInternalUserForActiveOrganisationByEmail.GETInternalUserForActiveOrganisationByEmail
                    ).pause(Int_Ext_SCNPaceMin seconds, Int_Ext_SCNPaceMax seconds)



  val deleteOrganisation = scenario("Delete Organisation")
    .exec(IDAMHelper.getIdamTokenLatest).exec(S2SHelper.S2SAuthToken).exec(DeleteOrganisation.DeleteOrganisation)

  val getUsersbyOrg = scenario("Get Users By org")
                           .exec(IDAMHelper.getIdamTokenLatest).exec(S2SHelper.S2SAuthToken).exec(External_GETInternalUserForGivenOrganisations.GETUsersByOrganisation)

  val Legacy_strategic_SCN = scenario("Professional Reference Data - Legacy + Strategic")
                            .exec(
                                Internal_GETPbas.GETPbas,
                                External_SearchPbas.SearchPbas,
                                External_GETPbas.GETPbas
                              )
                            .pause(Legacy_strategic_SCNPaceMin seconds, Legacy_strategic_SCNPaceMax seconds)

  /*setUp(
   Int_Scn.inject(atOnceUsers(2))
  //Ext_Scn.inject(atOnceUsers(1))
   // Int_Ext_SCN.inject(atOnceUsers(1)),
   // Legacy_strategic_SCN.inject(atOnceUsers(1))
  ).protocols(httpProtocol)*/

  setUp(
    getUsersbyOrg.inject(atOnceUsers(1))).protocols(httpProtocol)
}
