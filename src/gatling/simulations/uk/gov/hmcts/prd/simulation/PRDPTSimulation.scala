package uk.gov.hmcts.prd.simulation

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef.{exec, _}
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.external._
import uk.gov.hmcts.prd.internal._
import uk.gov.hmcts.prd.util.Environment._
import uk.gov.hmcts.prd.util.{CreateUser, IDAMHelper, S2SHelper}

import scala.concurrent.duration._

class PRDPTSimulation extends Simulation{

  val config: Config = ConfigFactory.load()
  val IntPaceMin = config.getString("internal.intPaceMin").toInt
  val IntPaceMax = config.getString("internal.intPaceMax").toInt
  
  val httpProtocol = http
    .baseUrl(BaseUrl)

  val Scn = scenario("Professional Reference Data")
    .exec(
      IDAMHelper.getIdamTokenLatest,
      S2SHelper.S2SAuthToken,
      CreateUser.createUser,
      Internal_CreateOrganisation.createOrganisation,
      Internal_DeleteOrganisation.DeleteOrganisation,
      Internal_CreateOrganisation.createOrganisation,
      Internal_UpdateOrganisation.updateOrganisation,
      Internal_GETOrganisationByID.GETOrganisationByID,
      CreateUser.createUser,
      Internal_AddInternalUserToOrg.AddInternalUserToOrg,
      Internal_GETInternalUserForGivenOrganisations.GETInternalUserForGivenOrganisations,
      Internal_GETAllOrganisation.GETAllOrganisation,
      Internal_GETInternalUserForActiveOrganisationByEmail.GETInternalUserForActiveOrganisationByEmail,
      Internal_GETOrganisationsByStatusACTIVE.GETOrganisationsByStatusACTIVE,
      Internal_GETOrganisationsByStatusPENDING.GETOrganisationsByStatusPENDING,
      Internal_GETPbas.GETPbas,
      Internal_EditPbas.EditPbas,
      Internal_EditUserRole.EditInternalUserRole,
      Internal_UpdateUserStatus.UpdateInternalUserStatus,

      CreateUser.createUser,
      External_CreateOrganisation.createOrganisation,
      Internal_UpdateOrganisation.updateOrganisation,
      IDAMHelper.getIdamTokenLatest2,
      External_GETOrganisation.GETOrganisation,
      CreateUser.createUser,
      External_AddInternalUserToOrg.AddInternalUserToOrg,
      External_GETInternalUserForGivenOrganisations.GETInternalUserForGivenOrganisations,
      External_GETInternalUserForActiveOrganisationByEmail.GETInternalUserForActiveOrganisationByEmail,
      External_GETPbas.GETPbas,
      External_GETOrganisationsByStatusACTIVE.GETOrganisationsByStatusACTIVE,
      External_GETStatusInternalUserForActiveOrganisationByEmail.GETStatusInternalUserForActiveOrganisationByEmail,
      External_EditUserRole.EditInternalUserRole,
      External_UpdateUserStatus.UpdateInternalUserStatus,
    )
    .pause(IntPaceMin seconds, IntPaceMax seconds)

  setUp(
    Scn.inject(rampUsers(1) during (300))
  )
  .protocols(httpProtocol)

}