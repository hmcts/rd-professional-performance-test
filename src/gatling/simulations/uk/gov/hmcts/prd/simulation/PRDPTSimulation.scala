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
    .baseUrl(config.getString("baseUrl"))
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080))

  val Scn = scenario("Professional Reference Data")
    .exec(
      IDAMHelper.getIdamTokenLatest,
      S2SHelper.S2SAuthToken,
      Internal_CreateOrganisation.createOrganisation,
      Internal_UpdateOrganisation.updateOrganisation,
      Internal_GETOrganisationByID.GETOrganisationByID,
      Internal_AddInternalUserToOrg.AddInternalUserToOrg,
      Internal_GETInternalUserForGivenOrganisations.GETInternalUserForGivenOrganisations,
      Internal_GETAllOrganisation.GETAllOrganisation,
      Internal_GETInternalUserForActiveOrganisationByEmail.GETInternalUserForActiveOrganisationByEmail,
      Internal_GETOrganisationsByStatusACTIVE.GETOrganisationsByStatusACTIVE,
      Internal_GETOrganisationsByStatusPENDING.GETOrganisationsByStatusPENDING,
      Internal_GETPbas.GETPbas,
      Internal_EditPbas.EditPbas,
      Internal_UpdateUserStatus.internal_UpdateUserStatus,
      Internal_EditUserRole.EditInternalUserRole,
      DeleteOrganisation.DeleteOrganisation,
      External_CreateOrganisation.createOrganisation,
      External_UpdateOrganisation.updateOrganisation,
      External_GETOrganisationByID.GETOrganisationByID,
      External_AddInternalUserToOrg.AddInternalUserToOrg,
      External_GETInternalUserForGivenOrganisations.GETInternalUserForGivenOrganisations,
      External_GETInternalUserForActiveOrganisationByEmail.GETInternalUserForActiveOrganisationByEmail,
      External_GETPbas.GETPbas
    )
    .pause(IntPaceMin seconds, IntPaceMax seconds)

  setUp(
    Scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
   // .assertions(forAll.responseTime.percentile3.lte(500))
   // .assertions(global.failedRequests.percent.lte(5))

}
