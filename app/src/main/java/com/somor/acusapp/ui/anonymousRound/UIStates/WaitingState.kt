package com.somor.acusapp.ui.anonymousRound.UIStates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.somor.acusapp.ui.anonymousRound.AnonymousRoundViewModel
import com.somor.acusapp.ui.home.AutoResizedText

@Composable
fun WaitingState(
    roundId: String,
    playerId: String,
    questionTitle: String,
    accusedList: MutableList<String>,
    owner: Boolean,
    onNextQuestion: () -> Unit,
    anonymousRoundViewModel: AnonymousRoundViewModel

) {
    if (accusedList.isEmpty()) {
        AnswerState(
            roundId,
            playerId,
            questionTitle,
            accusedList,
            owner,
            onNextQuestion = { /*TODO*/ },
            anonymousRoundViewModel = anonymousRoundViewModel
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.size(16.dp))
            AutoResizedText(
                text = "Esperando al resto de jugadores...",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )

        }
    }
}