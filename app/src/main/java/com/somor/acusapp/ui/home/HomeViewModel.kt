package com.somor.acusapp.ui.home

import androidx.lifecycle.ViewModel
import com.somor.acusapp.data.network.FirebaseService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val firebaseService : FirebaseService) : ViewModel() {
    fun navigateToQuestions(navigateToQuestions: () -> Unit) {
        navigateToQuestions()
    }

    fun navigateToPublic(navigateToPublic: () -> Unit) {
        navigateToPublic()
    }

    fun navigateToCreateRound(navigateToCreateRound: (String, String)-> Unit) {
       val round =  firebaseService.insertNewRound() //Introduzco una nueva partida y me devuelve una ronda
        navigateToCreateRound(round.idRound, round.date)
    }

    fun navigateToJoinRound(navigateToJoinRound: () -> Unit) {
        navigateToJoinRound()
    }
}