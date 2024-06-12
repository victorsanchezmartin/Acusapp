package com.somor.acusapp.ui.anonymousRound.UIStates

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.somor.acusapp.ui.anonymousRound.AnonymousRoundViewModel
import com.somor.acusapp.ui.theme.NewRound
import com.somor.acusapp.ui.theme.wenKaiFontFamily


@Composable
fun AnswerState(
    roundId: String,
    playerId: String,
    questionPlaying: String,
    accusedList: List<String>,
    owner: Boolean,
    onNextQuestion: () -> Unit,
    anonymousRoundViewModel: AnonymousRoundViewModel
) {
    Log.d("collect listaStateAnswer", "State ANSWER: es propietario: ${owner}")


    if (accusedList.isEmpty() || questionPlaying.isEmpty()) {//cuando se ha salido del juego y quiere volver a entrar
        Log.d("collect listaStateAnswer", "AntesAnswer..............")
        anonymousRoundViewModel.getPlayerList(roundId)
        anonymousRoundViewModel.getQuestionPLaying()
        anonymousRoundViewModel.getListAccusedPlayers2(playerId)
        Log.d("collect listaStateAnswer", "DespuesAnswer..............")

    } else {

        val looserList: MutableList<String> = mutableListOf()
        val numbersByElement = accusedList.groupingBy { it }
            .eachCount()//map{[juagdor1,4], [jugador2,7], [juagor3,1]} La clave es el nombre de jugador y el valor es las repeticiones

        val sortedMap = numbersByElement.toList().sortedByDescending { it.second }.toMap()
        val maxValue =
            numbersByElement.values.max() //Saco el numero reperitdo más alto que encuentre. Ejemplo = 7
        for (map in numbersByElement) { //recorro el map
            if (map.value == maxValue) { //Por ejemplo si ["Jugador2", 7] == 7
                looserList.add(map.key)
            }
        }
        Log.d("collect listaStateAnswer", "State ANSWER: tamaño lista looser: ${looserList.size}")
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                textAlign = TextAlign.Justify, fontFamily = wenKaiFontFamily, color = Color.White,
                text = questionPlaying, fontSize = 24.sp
            )

            Box() {
                Column(
                    modifier = Modifier.padding(8.dp).align(Alignment.Center)
                ) {
                    sortedMap.forEach { (name, count) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .wrapContentHeight().align(Alignment.CenterVertically),
                                textAlign = TextAlign.Start,
                                text = name,
                                fontSize = 24.sp,
                                fontFamily = wenKaiFontFamily,
                                color = Color.White,
                            )
                            Text(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .wrapContentHeight().align(Alignment.CenterVertically),
                                textAlign = TextAlign.Start,
                                text = count.toString(),
                                fontSize = 24.sp,
                                fontFamily = wenKaiFontFamily,
                                color = Color.White,
                            )
                        }
                    }
                }

                if (owner) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            onNextQuestion()
                        },
                        text = { Text(text = "Siguiente pregunta", fontSize = 16.sp) },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Accuse"
                            )
                        },
                        containerColor = NewRound,
                        contentColor = Color.White,
                        expanded = true,
                        modifier = Modifier
                            .fillMaxWidth().align(Alignment.BottomCenter)
                    )
                }
            }

        }
    }
}