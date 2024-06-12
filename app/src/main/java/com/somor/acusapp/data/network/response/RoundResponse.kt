package com.somor.acusapp.data.network.response


import com.somor.acusapp.data.network.dto.QuestionAnsweredDto
import com.somor.acusapp.domain.RoundModel

data class RoundResponse(
    val idRound: String? = null,
    val listPlayers: List<PlayerResponse>? = null,
    val roundStarted : Boolean? = null,
    val date : String? = null,
    val idQuestionPlaying : String? = null,
    val listQuestionsAnswered : List<QuestionAnsweredResponse>? = null
) {
    fun toDomain(): RoundModel {
       return RoundModel( idRound = idRound ?: "no hay partidas",
                         listPlayers = listPlayers?.map { it.toDomain() } ?: mutableListOf(),//con esto le digo que si me devuelve un nulo me lo convierta a una lista mutable vacia
                        roundStarted = roundStarted ?: false,
                        date = date ?: "no se ha establecido la hora",
                        idQuestionPlaying = idQuestionPlaying ?: "no se ha encontrado pregunta",
                        listQuestionsAnswered = listQuestionsAnswered?.map { it.toDomain() } ?: mutableListOf()
       )
    }
}