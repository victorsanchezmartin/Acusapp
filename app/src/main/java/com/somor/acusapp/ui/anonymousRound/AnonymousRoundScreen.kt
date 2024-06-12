package com.somor.acusapp.ui.anonymousRound

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.somor.acusapp.ui.anonymousRound.UIStates.AnonymousRoundUIState
import com.somor.acusapp.ui.anonymousRound.UIStates.AnswerState
import com.somor.acusapp.ui.anonymousRound.UIStates.EndState
import com.somor.acusapp.ui.anonymousRound.UIStates.LoadingState
import com.somor.acusapp.ui.anonymousRound.UIStates.QuestionState
import com.somor.acusapp.ui.anonymousRound.UIStates.WaitingState

@Composable
fun AnonymousRoundScreen (
    anonymousRoundViewModel: AnonymousRoundViewModel = hiltViewModel(),
    roundId: String?,
    playerId: String?,
    owner: Boolean?,
    navigateToHomeScreen: () -> Unit
) {

    //El orden de preguntas lo asigno al creador de la partida. En los argumentos del Navigator paso los datos simples, y no el 
    //objeto Player porque se considera un antipatrón pasar datos complejos como argumentos.
    val iuState by anonymousRoundViewModel.iuState.collectAsState()
    val playerList by anonymousRoundViewModel.playerList.collectAsState()
    val showDialog by anonymousRoundViewModel.showDialog
    val accuseButtonEnabled by anonymousRoundViewModel.accuseButtonEnabled.collectAsState()
    val accusedList by anonymousRoundViewModel.accusedList.collectAsState()
    val questionTitle by anonymousRoundViewModel.questionTitle.collectAsState()
    val mostAccusedList by anonymousRoundViewModel.mostAccudedList.collectAsState()
    val playerSelected by anonymousRoundViewModel.playerSelected
    //Primer paso en cada recomposicion y la primera vez que se genera el Screen es comprobar qué estado hay en RT
    Log.d("collect listaScreen", "Antes Estados: estado: $iuState")
    anonymousRoundViewModel.getIUState(roundId!!, playerId!!, owner!!) //siempre flow
    Log.d("collect listaScreen", "Despues Estados: estado: $iuState")

        when (iuState) {
            is AnonymousRoundUIState.Error -> {}

            AnonymousRoundUIState.Question -> {
                Log.d("collect listaScreen", "pasandoQuestion")

                QuestionState(questionTitle,
                    playerList,
                    onPlayerSelected = { anonymousRoundViewModel.onPlayerSelected(it) },
                    accuseButtonEnabled = accuseButtonEnabled,
                    onAccuseButton = { anonymousRoundViewModel.onAccuseButton2(playerId) },
                    playerSelected)
            }
            AnonymousRoundUIState.Waiting -> {
                Log.d("collect listaScreen", "pasandoWaiting")
                WaitingState(  roundId,
                    playerId!!,
                    questionTitle,
                    accusedList,
                    owner!!,
                    anonymousRoundViewModel
                )
            }

            AnonymousRoundUIState.Answer -> {
                AnswerState(
                    roundId,
                    playerId!!,
                    questionTitle,
                    accusedList,
                    owner!!,
                    onNextQuestion = { anonymousRoundViewModel.onNextQuestion()},
                    anonymousRoundViewModel
                )
            }

            AnonymousRoundUIState.Loading -> {
                LoadingState()
            }
            AnonymousRoundUIState.End -> {
                EndState(mostAccusedList, accuseButtonEnabled){anonymousRoundViewModel.getMostAccused()}
            }
        }

    /*Antes de salir de la pantalla le pregunto al usuario si realmente quiere irse*/
    //por defecto lo ponemos a true para que esté habilitado
    OnBackPressed { anonymousRoundViewModel.onShowDialog() }
    ExitDialog(showDialog,
        onExit = {
            anonymousRoundViewModel.setFalseSelectedPlayer(
                roundId!!,
                playerId!!,
                navigateToHomeScreen
            )
        },
        onDismissDialog = { anonymousRoundViewModel.onDisMissDialog() })
}


@Composable
fun OnBackPressed( onShowDialog: () -> Unit) {
    BackHandler() {
        onShowDialog()
    }
}

    @Composable
    fun ExitDialog(showDialog: Boolean, onExit: () -> Unit, onDismissDialog: () -> Unit) {

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { onDismissDialog() },
                title = { Text("¿Seguro que quieres salir?") },
                text = { Text("Esta acción no se pordrá deshacer") },
                confirmButton = {
                    TextButton(onClick = { onExit() }) {
                        Text("Salir".uppercase())
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onDismissDialog() }) {
                        Text("No".uppercase())
                    }
                },
            )
        }
    }





