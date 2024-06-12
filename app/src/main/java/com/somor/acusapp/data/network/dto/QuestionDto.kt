package com.somor.acusapp.data.network.dto

data class QuestionDto(
    val id : String?, //Este es nulable porque lo recibo de RTDB
    val title: String,
    val type: String) {



}
