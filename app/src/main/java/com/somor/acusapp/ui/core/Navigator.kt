package com.somor.acusapp.ui.core

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.somor.acusapp.ui.anonymousRound.AnonymousRoundScreen
import com.somor.acusapp.ui.createRound.CreateRoundScreen
import com.somor.acusapp.ui.home.HomeScreen
import com.somor.acusapp.ui.joinRound.JoinRoundScreen
import com.somor.acusapp.ui.publicRound.RoundScreen
import com.somor.acusapp.ui.questions.QuestionScreen

@Composable
fun ContentWrapper(navigationController: NavHostController) {
    NavHost(navController = navigationController, startDestination = Routes.Home.route ){
        composable(Routes.Home.route){
            HomeScreen( navigateToQuestions = { navigationController.navigate(Routes.Questions.route)},
                        navigateToPublic = {navigationController.navigate(Routes.PublicRound.route)},
                        navigateToCreateRound = {roundId, date ->
                            navigationController.navigate(Routes.CreateRound.createRoute(roundId, date))},
                        navigateToJoinRound = {navigationController.navigate(Routes.JoinRound.route)}

                      )
        }
        composable(Routes.PublicRound.route){
            RoundScreen(){navigationController.navigate(Routes.Home.route)}
        }
        composable(Routes.Questions.route){
            QuestionScreen()
        }
        composable(Routes.JoinRound.route){

            JoinRoundScreen(
                navigateToAnonymousRound = {roundId, playerId, owner ->
                navigationController.navigate(Routes.AnonymousRound.createRoute(roundId, playerId, owner))})
            }

        composable(Routes.CreateRound.route,
            arguments = listOf(
                navArgument ("roundId" ){type = NavType.StringType},
                navArgument ("date" ){type = NavType.StringType}

            )){
           CreateRoundScreen(roundId = it.arguments?.getString("roundId").orEmpty(),
               date = it.arguments?.getString("date").orEmpty(),
               navigateToAnonymousRound = {roundId, playerId, owner ->
                   navigationController.navigate(Routes.AnonymousRound.createRoute(roundId, playerId, owner))})

        }

        composable(Routes.AnonymousRound.route,
            arguments = listOf(
                navArgument("roundId"){type = NavType.StringType},
                navArgument("playerId"){type = NavType.StringType},
                navArgument("owner"){type = NavType.BoolType}
            )){
            AnonymousRoundScreen(roundId = it.arguments?.getString("roundId").orEmpty(),
                            playerId = it.arguments?.getString("playerId").orEmpty(),
                                owner = it.arguments?.getBoolean("owner")?.or(false))
            { navigationController.navigate(Routes.Home.route) }
        }
    }
}
/**
* Creo una sealed class a modo de constantes. Aquí escribo las rutas y en la clase Navigator hago referencia a estas
 */
sealed class Routes(val route : String) {
    data object Home: Routes("home")
    data object CreateRound: Routes("createRound/{roundId}/{date}") {
        fun createRoute(roundId : String, date : String):String{
            return "createRound/$roundId/$date"
        }
    }
    data object AnonymousRound: Routes("anonymous/{roundId}/{playerId}/{owner}"){
        fun createRoute(roundId : String, playerId: String, owner : Boolean) :String{
            return "anonymous/$roundId/$playerId/$owner"
        }
    }
    data object PublicRound: Routes("publicRound")
    data object JoinRound: Routes("join")
    data object Questions: Routes("questions")
}
