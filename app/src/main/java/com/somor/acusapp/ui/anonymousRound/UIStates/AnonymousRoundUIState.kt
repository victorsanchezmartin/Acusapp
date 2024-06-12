package com.somor.acusapp.ui.anonymousRound.UIStates

sealed class AnonymousRoundUIState {
    data object Waiting : AnonymousRoundUIState()
    data object Loading : AnonymousRoundUIState()
    data object Question: AnonymousRoundUIState()
    data object Answer : AnonymousRoundUIState()
    data object End : AnonymousRoundUIState()
    data class Error(val msg : String) : AnonymousRoundUIState()
}