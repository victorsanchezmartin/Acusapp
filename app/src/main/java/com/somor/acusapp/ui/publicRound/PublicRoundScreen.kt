package com.somor.acusapp.ui.publicRound

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.somor.acusapp.ui.anonymousRound.OnBackPressed
import com.somor.acusapp.ui.theme.NewRound
import com.somor.acusapp.ui.theme.wenKaiFontFamily

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RoundScreen(roundViewModel: PublicRoundViewModel = hiltViewModel(),
                navigateToHomeScreen: () -> Unit) {

    //Lo primero que quiero es conseguir el listado de preguntas y que solo se haga una vez al principio del juego
    LaunchedEffect(true) {
        roundViewModel.getQuestions() //En este metodo también barajo el listado, así ninguna ronda será igual a la anterior
    }
    val isListLoaded by roundViewModel.isLoadedList.observeAsState()//estado para mostrar la pregunta o un texto de "cargando"
    val runningOrder by roundViewModel.runningOrder.observeAsState(0)
    val list by roundViewModel.questionList.collectAsState()
    val interactionSource = remember { MutableInteractionSource() } //Esta variable la necesito para quitar el ripple cuando pulsas
    val previousButtonEnabled = roundViewModel.previousButtonEnabled.value
    val showDialog = roundViewModel.showDialog.value
    val onBackEffect by roundViewModel.onBackEffect

        if (isListLoaded == true) { //Para quitar == mirar TicTacToe
            if (runningOrder != list.size) {
                Box(contentAlignment = Alignment.Center
                        ) {
                        AnimatedContent(
                            modifier = Modifier.clickable(
                                interactionSource = interactionSource,
                                indication = null
                            )//Para quitar el ripple
                            {
                                roundViewModel.onNextQuestion()
                            },
                            targetState = runningOrder,
                            transitionSpec = {
                                if(onBackEffect){
                                    slideInHorizontally{ -it } togetherWith slideOutHorizontally {it}
                                }else {
                            slideInHorizontally{ it } togetherWith slideOutHorizontally {-it}}
                            }, label = "Slide question"
                        ) { runningOrder ->
                            Text(
                                text = list[runningOrder].title,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = wenKaiFontFamily,
                                fontSize = 32.sp,
                                lineHeight = 40.sp,
                                modifier = Modifier
                                    .padding(vertical = 120.dp)
                                    .fillMaxHeight()
                                    .padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    if(previousButtonEnabled){
                        ExtendedFloatingActionButton(
                            onClick = {roundViewModel.onPreviousQuestion()},
                            icon = { Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back question") },
                            text = { Text(text = "Pregunta anterior", fontSize =16.sp)},
                            containerColor = NewRound,
                            contentColor = Color.White,

                            expanded = true,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(32.dp)
                        )
                    }
                }
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text  = "FIN DEL JUEGO",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = wenKaiFontFamily,
                        fontSize = 32.sp
                    )
                }

            }
    } else {
        Box(Modifier.fillMaxSize()) {
            Text("CARGANDO PREGUNTAS...", modifier = Modifier.align(Alignment.Center))
        }
    }
    OnBackPressed { roundViewModel.onBackPressed() }
    if(showDialog){

        ExitDialogPublic(
            { roundViewModel.onDismissDialog() },
            { roundViewModel.onConfirmation(navigateToHomeScreen) }, dialogTitle  = "¿Quieres volver al menú?",
            icon = Icons.Default.Info
        )
    }
}

@Composable
fun ExitDialogPublic(
    onDismissDialog:() -> Unit,
    onConfirmation:() -> Unit,
    dialogTitle: String,
    icon: ImageVector
) {
        AlertDialog(
            icon = {
                Icon(icon, contentDescription = "Icono información")
            },
            title = {
                Text(text = dialogTitle)
            },

            onDismissRequest = {
                onDismissDialog()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmation()
                    }
                ) {
                    Text("Volver")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissDialog()
                    }
                ) {
                    Text("Quedarme")
                }
            }
        )
}
