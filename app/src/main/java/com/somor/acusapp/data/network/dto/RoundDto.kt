package com.somor.acusapp.data.network.dto

data class RoundDto (val idRound: String,
                     val listPlayers : List<PlayerDto>,
                     val roundStarted: Boolean,
                     val date : String,
                     val idQuestionPlaying : String,
                     val listQuestionsAnswered : List<QuestionAnsweredDto>)