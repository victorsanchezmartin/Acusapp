package com.somor.acusapp.ui.joinRound

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.somor.acusapp.domain.PlayerModel
import com.somor.acusapp.domain.RoundModel
import com.somor.acusapp.ui.home.AutoResizedText
import com.somor.acusapp.ui.theme.NewRound
import com.somor.acusapp.ui.theme.wenKaiFontFamily


@Composable
fun JoinRoundScreen(
    joinRoundViewModel: JoinRoundViewModel = hiltViewModel(),
    navigateToAnonymousRound: (String, String, Boolean) -> Unit
) {
    val idRound by joinRoundViewModel.idRound.observeAsState("")
    val buttonEnabled by joinRoundViewModel.buttonEnabled.observeAsState(false)
    val roundList by joinRoundViewModel.roundList.collectAsState()
    val playerList by joinRoundViewModel.playerList.collectAsState()
    val playerNameSelected by joinRoundViewModel.playerNameSelected.collectAsState()

    joinRoundViewModel.getPlayerList(idRound) //empiezo a cargar el listado de jugadores cuando hay seleccionado un id Round

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {

        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AutoResizedText(
                text = "Selecciona una partida",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )
            LaunchedEffect(true) {//Cargo una vez las rondas
                joinRoundViewModel.getRounds()
            }

            LazyRow(modifier = Modifier.padding(8.dp)) {
                items(roundList.reversed()) { round -> //Ordeno la lista para que siempre aparezca primero la última ronda.
                    ItemRound(
                        idRound,
                        round,
                        onItemClick = { joinRoundViewModel.onSelectedRound(it) })
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
            PlayerName(
                playerList = playerList,
                playerNameSelected,
                onPlayerSelected = { joinRoundViewModel.onPlayerSelected(it) })
            Spacer(modifier = Modifier.size(8.dp))

        }
        ExtendedFloatingActionButton(
            onClick = {
                if (buttonEnabled) {
                    joinRoundViewModel.onButtonClick(idRound, navigateToAnonymousRound)
                }
            },
            icon = { Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "join game") },
            text = { Text(text = "Unirse", fontSize = 16.sp) },
            containerColor = NewRound,
            contentColor = Color.White,
            expanded = buttonEnabled,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        )

    }
}

@Composable
fun PlayerName(
    playerList: MutableList<PlayerModel>,
    playerNameSelected: String,
    onPlayerSelected: (PlayerModel) -> Unit
) {
    if (playerList.isNotEmpty()) {
        AutoResizedText(text = "Selecciona tu nombre", style = MaterialTheme.typography.headlineLarge, color = Color.White )
        val playerNoOwnerList = buildList { addAll(playerList) }.toMutableList()
        for (player in playerList){
            if(player.owner) { //bien hecho estaba como player.owner
                playerNoOwnerList.remove(player)
            }
        }
    LazyVerticalGrid(
        modifier = Modifier.fillMaxWidth().padding(bottom = 64.dp),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(playerNoOwnerList) {

           var  modifier = Modifier
                .padding(8.dp)
                .width(125.dp)
                .height(100.dp)
                .clickable {onPlayerSelected(it)
                }
                var backgroundColor = Color.White
                var textColor = Color.Black
                if (it.playerName == playerNameSelected) { //hago una comprobacion para saber cuál es el item seleccionado
                    backgroundColor = NewRound
                    textColor = Color.White
                }
                if(it.selected){
                    textColor = Color.White
                    backgroundColor = Color.Gray
                     modifier = Modifier.padding(8.dp)
                        .width(125.dp)
                        .height(100.dp)
                }
                Card(
                    modifier = modifier,
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 16.dp
                    )
                ){

                    Box(modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor), contentAlignment = Alignment.Center ){
                        Text(text = it.playerName, fontFamily = wenKaiFontFamily, textAlign = TextAlign.Center, fontSize = 20.sp, color = textColor)
                    }
                }
            }
    }
    }
}

@Composable
fun ItemRound(idRound: String, round: RoundModel, onItemClick: (String) -> Unit) {
    var backgroundColor = Color.White
    var textColor = Color.Black
    if (round.idRound == idRound) { //hago una comprobacion para saber cuál es el item seleccionado
        backgroundColor = NewRound
        textColor = Color.White
    }
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(125.dp)
            .height(100.dp)
            .clickable {
                onItemClick(round.idRound)
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 16.dp
        )
    ) {

        Box(modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor), contentAlignment = Alignment.Center ){
            Text(text = round.date, fontFamily = wenKaiFontFamily, textAlign = TextAlign.Center, fontSize = 20.sp, color = textColor)
        }
    }
}
