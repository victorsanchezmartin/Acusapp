package com.somor.acusapp.ui.anonymousRound.UIStates

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.somor.acusapp.ui.home.AutoResizedText

@Composable
fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AutoResizedText(text = "Cargando datos...", style =  MaterialTheme.typography.headlineLarge, color = Color.White)
    }
}