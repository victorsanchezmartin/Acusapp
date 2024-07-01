package com.somor.acusapp.ui.core

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.somor.acusapp.ui.anonymousRound.AnonymousRoundScreen
import com.somor.acusapp.ui.createRound.CreateRoundScreen
import com.somor.acusapp.ui.home.HomeScreen
import com.somor.acusapp.ui.joinRound.JoinRoundScreen
import com.somor.acusapp.ui.publicRound.RoundScreen
import com.somor.acusapp.ui.questions.QuestionScreen

@Composable
fun ContentWrapper(navigationController: NavHostController) {
    NavHost(navController = navigationController, startDestination = Home){
        composable<Home>{
            HomeScreen(
                        navigateToPublic = {
                            navigationController.navigate(PublicRoundScreen)},
                        navigateToCreateRound = {roundId, date ->
                            navigationController.navigate(CreateRoundScreen(roundId, date))},
                        navigateToJoinRound = {navigationController.navigate(JoinRoundScreen)},
                      navigateToQuestions = { navigationController.navigate(QuestionScreen)}
                      )
        }

        composable<PublicRoundScreen>{
            RoundScreen{navigationController.navigate(Home)}
        }
        composable<QuestionScreen>{
            QuestionScreen()
        }

        composable<JoinRoundScreen>{
            JoinRoundScreen(
                navigateToAnonymousRound = { roundId, playerId, owner ->
                navigationController.navigate(AnonymousRound(
                    roundId,
                    playerId,
                    owner))})
            }

        composable<CreateRoundScreen>{
            val createRoundScreen = it.toRoute<CreateRoundScreen>()
           CreateRoundScreen(
               roundId = createRoundScreen.roundId,
               date = createRoundScreen.date,
               navigateToAnonymousRound = {roundId, playerId, owner ->
                   navigationController.navigate(AnonymousRound(roundId, playerId, owner))})

        }

        composable<AnonymousRound>{
            val anonymous = it.toRoute<AnonymousRound>()
            AnonymousRoundScreen(roundId = anonymous.roundId,
                            playerId = anonymous.playerId,
                                owner = anonymous.owner)
            { navigationController.navigate(Home) }
        }
    }
}
