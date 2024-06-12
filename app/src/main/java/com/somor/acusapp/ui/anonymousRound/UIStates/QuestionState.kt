package com.somor.acusapp.ui.anonymousRound.UIStates

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.somor.acusapp.domain.PlayerModel
import com.somor.acusapp.ui.theme.NewRound
import com.somor.acusapp.ui.theme.wenKaiFontFamily

@Composable
fun QuestionState(
    questionTitle: String,
    playerList: List<PlayerModel>,
    onPlayerSelected: (PlayerModel) -> Unit,
    accuseButtonEnabled: Boolean,
    onAccuseButton: () -> Unit,
    playerSelected: PlayerModel,
) {
    Box(contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                textAlign = TextAlign.Center, color = Color.White,
                text = questionTitle, fontSize = 24.sp
            )
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 64.dp),
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(playerList) {
                    var modifier = Modifier
                        .padding(8.dp)
                        .width(125.dp)
                        .height(100.dp)
                        .clickable {
                            onPlayerSelected(it)
                        }
                    var backgroundColor = Color.White
                    var textColor = Color.Black
                    if (it.playerName == playerSelected.playerName) { //hago una comprobacion para saber cuál es el item seleccionado
                        backgroundColor = NewRound
                        textColor = Color.White
                    }
                    Card(
                        modifier = modifier,
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 16.dp
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(backgroundColor), contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = it.playerName,
                                fontFamily = wenKaiFontFamily,
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                color = textColor
                            )
                        }
                    }
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = {
                if (accuseButtonEnabled) {
                    onAccuseButton()
                    println("accused")
                }
            },
            text = { Text(text = "Acusar", fontSize = 16.sp) },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Accuse"
                )
            },
            containerColor = NewRound,
            contentColor = Color.White,
            expanded = accuseButtonEnabled,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}
