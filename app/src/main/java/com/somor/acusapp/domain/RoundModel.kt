package com.somor.acusapp.domain

import com.somor.acusapp.data.network.dto.QuestionAnsweredDto


data class RoundModel(
    val idRound: String,
    val listPlayers: List<PlayerModel>?,
    val roundStarted : Boolean,
    val date : String,
    val idQuestionPlaying : String,
    val listQuestionsAnswered : List<QuestionAnsweredModel>
) {

}