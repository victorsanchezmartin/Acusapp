package com.somor.acusapp.ui.anonymousRound.UIStates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.somor.acusapp.ui.home.AutoResizedText

/*
@Preview(showBackground = true, backgroundColor = 0xFF73082D)

 */
@Composable
fun EndState(
    @PreviewParameter(SampleUserProvider::class) mostAccusedList: MutableList<String>,
    accuseButtonEnabled: Boolean,
    getMostAccused: () -> Unit
) {

    if(mostAccusedList.isNotEmpty()){//la primera vez que pinta EndState no tiene cargada la lista del más acusado
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(modifier = Modifier
                .weight(1f)
                .fillMaxWidth()) {
                AutoResizedText(text = "FIN DEL JUEGO", style = MaterialTheme.typography.headlineLarge, color = Color.White, modifier = Modifier.fillMaxWidth() )
                AutoResizedText(text = "Resultado de las acusaciones", style = MaterialTheme.typography.headlineLarge, color = Color.White, modifier = Modifier.fillMaxWidth())
            }
            //Creo un map que contiene clave(nombre) y valor (repeticiones)
            val map = mostAccusedList.groupingBy { it }.eachCount()
            val sorted = map.toList().sortedByDescending {
                it.second
            }

            LazyColumn(modifier = Modifier.weight(4f)) {
                    items(sorted) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 32.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            AutoResizedText(text = it.first, style = MaterialTheme.typography.headlineLarge, color = Color.White, modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f), textAlign = TextAlign.Start )
                            AutoResizedText(text = it.second.toString(), style = MaterialTheme.typography.headlineLarge, color = Color.White, modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f), textAlign = TextAlign.End )

                        }
                    }
                }
            Column(modifier = Modifier.weight(1f)) {
                var shot = 0
                sorted.forEachIndexed {index, pair ->
                    when(index){
                        0 ->{
                            shot = 3
                        }
                        1 -> {
                            shot = 2
                        }
                        2 -> {
                            shot = 1
                        }
                    }
                    if(index == 0 || index == 1 || index == 2)
                        AutoResizedText(text = "${pair.first} bebe $shot tragos", style = MaterialTheme.typography.titleMedium, color = Color.White, modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f) )
                }
                }
            }
       
    }else{
       getMostAccused() //una vez que ya tiene la lista, en este metodo se recompone el estado _mostAccused, y cuando vuelva
        //a esta pantalla ya entrará por el if

        LoadingState()
    }
}
class SampleUserProvider: PreviewParameterProvider<MutableList<String>> {
    override val values = sequenceOf(
        mutableListOf("matty", "kellen", "matty", "rose", "matty", "kellen", "matty", "matty", "matty", "kellen", "shur", "klumpo", "lor", "pep", "july", "xcamur", "arrent", "lwert", "aback")
    )
}
