package simulations

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef.{exec, _}
import io.gatling.http.Predef._
import scenarios.external._
import scenarios.internal._
import scenarios.location._
import scenarios.caseworker._
import scenarios.judicial._
import utils._
import io.gatling.core.controller.inject.open.OpenInjectionStep
import io.gatling.commons.stats.assertion.Assertion
import io.gatling.core.pause.PauseType
import scala.concurrent.duration._

class PRDPTSimulation extends Simulation{

  val config: Config = ConfigFactory.load()
	/* TEST TYPE DEFINITION */
	/* pipeline = nightly pipeline against the AAT environment (see the Jenkins_nightly file) */
	/* perftest (default) = performance test against the perftest environment */
	val testType = scala.util.Properties.envOrElse("TEST_TYPE", "perftest")

	//set the environment based on the test type
	val environment = testType match{
		case "perftest" => "perftest" //perftest
		case "pipeline" => "perftest"
		case _ => "**INVALID**"
	}

	/* ******************************** */
	/* ADDITIONAL COMMAND LINE ARGUMENT OPTIONS */
	val debugMode = System.getProperty("debug", "off") //runs a single user e.g. ./gradlew gatlingRun -Ddebug=on (default: off)
	val env = System.getProperty("env", environment) //manually override the environment aat|perftest e.g. ./gradlew gatlingRun -Denv=aat
	/* ******************************** */

	/* PERFORMANCE TEST CONFIGURATION */
	val prdInternalTargetPerHour:Double = 100 //360
  val prdOtherInternalTargetPerHour:Double = 20
	val prdExternalTargetPerHour:Double = 100 //360
  val ldTargetPerHour:Double = 200 //200
  val jrdTargetPerHour:Double = 841
  val crdTargetPerHour:Double = 20 //20

	val rampUpDurationMins = 5 //5
	val rampDownDurationMins = 5 //5
	val testDurationMins = 60 //60

	val numberOfPipelineUsers = 5
	val pipelinePausesMillis:Long = 3000 //3 seconds

  val feedServices = csv("JudicialServices.csv").random

	//Determine the pause pattern to use:
	//Performance test = use the pauses defined in the scripts
	//Pipeline = override pauses in the script with a fixed value (pipelinePauseMillis)
	//Debug mode = disable all pauses
	val pauseOption:PauseType = debugMode match{
		case "off" if testType == "perftest" => constantPauses
		case "off" if testType == "pipeline" => customPauses(pipelinePausesMillis)
		case _ => disabledPauses
	}

  val httpProtocol = http
		.baseUrl(Environment.BaseUrl.replace("#{env}", s"${env}"))
		.inferHtmlResources()
		.silentResources
    .disableCaching

	before{
		println(s"Test Type: ${testType}")
		println(s"Test Environment: ${env}")
		println(s"Debug Mode: ${debugMode}")
	}

	val PRDInternalScenario = scenario("PRD Internal Scenario")
		.exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(
        CreateUser.createAdminUser,
        IDAMHelper.getAdminIdamToken,
        S2SHelper.S2SAuthToken,
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
        Internal_GetOrganisation.GETOrg,
        Internal_UpdatePBAStatus.Update,
        Internal_EditUserRole.EditInternalUserRole,
        Internal_UpdateUserStatus.UpdateInternalUserStatus,
        Internal_GETCaseFlags.GetCaseFlags,
        CreateUser.deleteAdminUser,
        CreateUser.deleteNewUser
      )
    }

  val PRDInternalOtherScenario = scenario("PRD Internal Other Scenario")
		.exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(
        CreateUser.createAdminUser,
        IDAMHelper.getAdminIdamToken,
        S2SHelper.S2SAuthToken,
        Internal_CreateOrganisation.createOtherOrganisation,
        Internal_UpdateOrganisation.updateOtherOrganization,
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
        Internal_GetOrganisation.GETOrg,
        Internal_UpdatePBAStatus.Update,
        Internal_EditUserRole.EditInternalUserRole,
        Internal_UpdateUserStatus.UpdateInternalUserStatus,
        Internal_GETCaseFlags.GetCaseFlags,
        CreateUser.deleteAdminUser,
        CreateUser.deleteNewUser
      )
    }
    

  val PRDExternalScenario = scenario("PRD External Scenario")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(
        CreateUser.createAdminUser,
        IDAMHelper.getAdminIdamToken,
        S2SHelper.S2SAuthToken,
        External_CreateOrganisation.createOrganisation,
        Internal_UpdateOrganisation.updateOrganisation,
        External_GETOrganisation.GETOrganisation,
        CreateUser.createUser,
        External_AddInternalUserToOrg.AddInternalUserToOrg,
        External_GETPbas.GETPbas,
        External_AddMultiplePBAs.AddMultiplePbas,
        External_DeletePBA.DeletePBA,
        External_GETInternalUserForGivenOrganisations.GETInternalUserForGivenOrganisations,
        External_GETInternalUserForActiveOrganisationByEmail.GETInternalUserForActiveOrganisationByEmail,
        External_GETOrganisation.GETOrganisation,
        External_GETOrganisationsByStatusACTIVE.GETOrganisationsByStatusACTIVE,
        External_GETStatusInternalUserForActiveOrganisationByEmail.GETStatusInternalUserForActiveOrganisationByEmail,
        External_EditUserRole.EditInternalUserRole,
        External_UpdateUserStatus.UpdateInternalUserStatus,
        CreateUser.deleteAdminUser,
        CreateUser.deleteNewUser
      )
		}

  val LDScenario = scenario("Location Ref Data Scenario")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(
        IDAMHelper.getIdamTokenLatest,
        S2SHelper.s2s("rd_location_ref_api"),
        LRD_ApiController.GetBuildingLocations,
        LRD_ApiController.GetRegions
        )
      .exec(LRD_VenueController.CourtVenueSearch)
      .repeat(5) {
        exec(LRD_ApiController.GetOrgServices)
      }
      .repeat(11) {
        exec(LRD_VenueController.GetCourtVenues)
        .exec(LRD_VenueController.CourtVenueServiceSearch)
      }
    }

  val JRDScenario = scenario("Judicial Ref Data Scenario")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .feed(feedServices)
      .exec(
        IDAMHelper.getJrdIdamToken,
        S2SHelper.s2s("rd_judicial_api"),
        Judicial_Users.JudicialPostUsers)
      .repeat(2) { 
        exec(Judicial_Users.JudicialPostUsersSearch)
      }
    } 

	/*===============================================================================================
	* Simulation Configuration
	 ===============================================================================================*/

	def simulationProfile(simulationType: String, userPerHourRate: Double, numberOfPipelineUsers: Double): Seq[OpenInjectionStep] = {
		val userPerSecRate = userPerHourRate / 3600
		simulationType match {
			case "perftest" =>
				if (debugMode == "off") {
					Seq(
						rampUsersPerSec(0.00) to (userPerSecRate) during (rampUpDurationMins minutes),
						constantUsersPerSec(userPerSecRate) during (testDurationMins minutes),
						rampUsersPerSec(userPerSecRate) to (0.00) during (rampDownDurationMins minutes)
					)
				}
				else{
					Seq(atOnceUsers(1))
				}
			case "pipeline" =>
				Seq(rampUsers(numberOfPipelineUsers.toInt) during (2 minutes))
			case _ =>
				Seq(nothingFor(0))
		}
	}

  //defines the test assertions, based on the test type
  def assertions(simulationType: String): Seq[Assertion] = {
    simulationType match {
      case "perftest" =>
        if (debugMode == "off") {
          Seq(global.successfulRequests.percent.gte(95),
            details("RD18_Internal_UpdateUserStatus").successfulRequests.count.gte((prdInternalTargetPerHour * 0.9).ceil.toInt),
            details("RD30_External_UpdateUserStatus").successfulRequests.count.gte((prdExternalTargetPerHour * 0.9).ceil.toInt)
          )
        }
        else{
          Seq(global.successfulRequests.percent.gte(95),
            details("RD18_Internal_UpdateUserStatus").successfulRequests.count.is(1),
            details("RD30_External_UpdateUserStatus").successfulRequests.count.is(2)
          )
        }
      case "pipeline" =>
        Seq(global.successfulRequests.percent.gte(95),
            forAll.successfulRequests.percent.gte(90)
        )
      case _ =>
        Seq()
    }
  }

	setUp(
		PRDInternalScenario.inject(simulationProfile(testType, prdInternalTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
    PRDInternalOtherScenario.inject(simulationProfile(testType, prdOtherInternalTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
		PRDExternalScenario.inject(simulationProfile(testType, prdExternalTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
    LDScenario.inject(simulationProfile(testType, ldTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
    JRDScenario.inject(simulationProfile(testType, jrdTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),		
	)
  .protocols(httpProtocol)
  .assertions(assertions(testType))
  .maxDuration(85.minutes)
}