package com.somor.acusapp.ui.createRound

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.somor.acusapp.domain.PlayerModel
import com.somor.acusapp.ui.home.AutoResizedText
import com.somor.acusapp.ui.theme.NewRound
import com.somor.acusapp.ui.theme.wenKaiFontFamily


@Composable
fun CreateRoundScreen (createRoundViewModel: CreateRoundViewModel = hiltViewModel(),
                       roundId : String,
                        date : String,
                       navigateToAnonymousRound: (String, String, Boolean) -> Unit){
    val name by createRoundViewModel.onTextChanged.observeAsState("")
    val playerList by createRoundViewModel.playerList.collectAsState()
    val startButtonEnabled by createRoundViewModel.startButtonEnabled.collectAsState()
    val playerOwner by createRoundViewModel.playerOwner.collectAsState()
    createRoundViewModel.loadPlayerList(roundId)
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        createRoundViewModel.toastMessage.collect { message ->
                Toast.makeText(context,message,Toast.LENGTH_SHORT,).show()
            }
    }


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally){
        RoundInformation(date)
        Spacer(modifier = Modifier.size(16.dp))
        InsertPlayer(name, onTextChanged = {createRoundViewModel.onTextChanged(it)})
        Spacer(modifier = Modifier.size(8.dp))
        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {

            AddPlayerButton(roundId, createRoundViewModel, name)
            StartRound(onStartRound = {createRoundViewModel.onStartRound(roundId, navigateToAnonymousRound)},
                         startButtonEnabled = startButtonEnabled,

            )
        }
        PlayersName(playerList,playerOwner) { createRoundViewModel.onPlayerSelected(it , roundId) }
    }
}



@Composable
fun PlayersName(playerList: List<PlayerModel>, playerOwner : String, onPlayerSelected: (PlayerModel) -> Unit) {
    if (playerList.isNotEmpty()){
        AutoResizedText(text = "Selecciona al creador de la partida:", style = MaterialTheme.typography.headlineLarge , color = Color.White)
    }

    LazyVerticalGrid(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(playerList) {

            var  modifier = Modifier
                .padding(8.dp)
                .width(125.dp)
                .height(100.dp)
                .clickable {onPlayerSelected(it)
                }
            var backgroundColor = Color.White
            var textColor = Color.Black
            if (it.playerName == playerOwner) { //hago una comprobacion para saber cuál es el item seleccionado
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

@Composable
fun RoundInformation(date: String) {
    AutoResizedText(text = "Has creado una partida nueva a las:", style = MaterialTheme.typography.headlineLarge , color = Color.White )
    AutoResizedText(text = date, style = MaterialTheme.typography.headlineLarge ,  color = Color.White)

}

@Composable
fun InsertPlayer(ownerNameState : String, onTextChanged:(String) -> Unit) {
    Box {
        OutlinedTextField(value = ownerNameState,
            onValueChange = { onTextChanged(it) },
            label = { Text(text = "Introduce los jugadores", fontSize = 12.sp) })
    }
}

@Composable
fun AddPlayerButton(roundId : String, createRoundViewModel: CreateRoundViewModel, name : String) {
    val buttonEnabled by createRoundViewModel.buttonAddPlayerEnabled.observeAsState(false)

    ExtendedFloatingActionButton(
        onClick = {
            if (buttonEnabled) {
                createRoundViewModel.insertPlayer(roundId, name)
            }
        },
        icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = "add player") },
        text = { Text(text = "Añadir", fontSize = 16.sp) },
        containerColor = NewRound,
        contentColor = Color.White,
        expanded = buttonEnabled,
        modifier = Modifier
            .padding(16.dp)
    )

}
@Composable
fun StartRound(
    startButtonEnabled: Boolean,
    onStartRound: () -> Unit
) {


    ExtendedFloatingActionButton(
        onClick = {
            if (startButtonEnabled) {
                onStartRound()
            }
        },
        icon = { Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "start") },
        text = { Text(text = "Empezar", fontSize = 16.sp) },
        containerColor = NewRound,
        contentColor = Color.White,
        expanded = startButtonEnabled,
        modifier = Modifier
            .padding(16.dp)
    )

}




