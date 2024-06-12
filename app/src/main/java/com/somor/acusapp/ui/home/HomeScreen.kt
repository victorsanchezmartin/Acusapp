package com.somor.acusapp.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import androidx.hilt.navigation.compose.hiltViewModel
import com.somor.acusapp.R
import com.somor.acusapp.ui.theme.JoinRound
import com.somor.acusapp.ui.theme.NewRound
import com.somor.acusapp.ui.theme.PublicRound
import com.somor.acusapp.ui.theme.Questions
import com.somor.acusapp.ui.theme.wenKaiFontFamily


@Composable
fun HomeScreen(homeViewModel: HomeViewModel = hiltViewModel(),
               navigateToQuestions:() -> Unit,
               navigateToPublic: () -> Unit,
               navigateToCreateRound: (String, String) -> Unit,
               navigateToJoinRound:() -> Unit) {
    val scrollState = rememberScrollState()
    var style: TextStyle = MaterialTheme.typography.bodyMedium
    var resizedTextStyle by remember {
        mutableStateOf(style)
    }
    var shouldDraw by remember {
        mutableStateOf(false)
    }
    val defaultFontSize = MaterialTheme.typography.bodyMedium.fontSize

    Column (modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {

        Card (modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .height(150.dp).clickable {homeViewModel.navigateToCreateRound(navigateToCreateRound)  }, elevation = CardDefaults.cardElevation(10.dp)) {
            Box( Modifier.background(color = NewRound), contentAlignment = Alignment.Center ){
                Image(painter = painterResource(id = R.drawable.new_round), contentDescription = "new round", modifier = Modifier.fillMaxSize().padding(4.dp))
                AutoResizedText(
                    text = "CREAR PARTIDA",
                    style = MaterialTheme.typography.headlineLarge,
                )
            }
        }

        Card (modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .height(150.dp).clickable {homeViewModel.navigateToJoinRound(navigateToJoinRound)  }, elevation = CardDefaults.cardElevation(32.dp)) {
            Box( Modifier.background(color = JoinRound), contentAlignment = Alignment.Center ){
                Image(painter = painterResource(id = R.drawable.anonymous), contentDescription = "join round", modifier = Modifier.fillMaxSize().padding(4.dp))
                AutoResizedText(
                    text = "UNIRSE",
                    style = MaterialTheme.typography.headlineLarge,
                )
            }
        }
        Card (modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .height(150.dp).clickable {homeViewModel.navigateToPublic(navigateToPublic)  }, elevation = CardDefaults.cardElevation(32.dp)) {
            Box( Modifier.background(color = PublicRound), contentAlignment = Alignment.Center ){
                Image(painter = painterResource(id = R.drawable.public_game), contentDescription = "public game", modifier = Modifier.fillMaxSize().padding(4.dp))
                AutoResizedText(
                    text = "PÚBLICO",
                    style = MaterialTheme.typography.headlineLarge,
                )
            }
        }
        Card (modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .height(150.dp).clickable {homeViewModel.navigateToQuestions(navigateToQuestions)  }, elevation = CardDefaults.cardElevation(32.dp)) {
            Box( Modifier.background(color = Questions), contentAlignment = Alignment.Center ){
                Image(painter = painterResource(id = R.drawable.questions), contentDescription = "Ver Preguntas", modifier = Modifier.fillMaxSize().padding(4.dp))
                AutoResizedText(
                    text = "VER PREGUNTAS",
                    style = MaterialTheme.typography.headlineLarge,
                )
            }
        }
    }
}
@Composable
fun AutoResizedText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = style.color,
    textAlign: TextAlign = TextAlign.Center
) {
    var resizedTextStyle by remember {
        mutableStateOf(style)
    }
    var shouldDraw by remember {
        mutableStateOf(false)
    }

    val defaultFontSize = MaterialTheme.typography.bodyLarge.fontSize

    Text(
        text = text,
        color = color,
        modifier = modifier.drawWithContent {
            if (shouldDraw) {
                drawContent()
            }
        },
        textAlign = textAlign,
        fontFamily = wenKaiFontFamily, fontWeight = FontWeight.Bold,
        softWrap = false,
        style = resizedTextStyle,
        onTextLayout = { result ->
            if (result.didOverflowWidth) {
                if (style.fontSize.isUnspecified) {
                    resizedTextStyle = resizedTextStyle.copy(
                        fontSize = defaultFontSize

                    )
                }
                resizedTextStyle = resizedTextStyle.copy(
                    fontSize = resizedTextStyle.fontSize * 0.95
                )
            } else {
                shouldDraw = true
            }
        }
    )
}

@Composable
fun AutoResizedText2(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = style.color,
    textAlign: TextAlign = TextAlign.Center
) {
    var resizedTextStyle by remember {
        mutableStateOf(style)
    }
    var shouldDraw by remember {
        mutableStateOf(false)
    }

    val defaultFontSize = MaterialTheme.typography.bodyLarge.fontSize

    Text(
        text = text,
        color = color,
        modifier = modifier.drawWithContent {
            if (shouldDraw) {
                drawContent()
            }
        },
        textAlign = textAlign,
        fontFamily = wenKaiFontFamily, fontWeight = FontWeight.Bold,
        softWrap = false,
        style = resizedTextStyle,
        onTextLayout = { result ->
            if (result.didOverflowHeight) {
                if (style.fontSize.isUnspecified) {
                    resizedTextStyle = resizedTextStyle.copy(
                        fontSize = defaultFontSize

                    )
                }
                resizedTextStyle = resizedTextStyle.copy(
                    fontSize = resizedTextStyle.fontSize * 0.95
                )
            } else {
                shouldDraw = true
            }
        }
    )
}