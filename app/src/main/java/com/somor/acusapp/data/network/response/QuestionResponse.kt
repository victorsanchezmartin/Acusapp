package com.somor.acusapp.data.network.response

import com.somor.acusapp.domain.QuestionModel

data class QuestionResponse (
    val id : String? = null, //Esta id la genera Firebase
    val title: String? = null,
    val type: String? = null
){

    //La información que me llega de RTDB la manejo aquí
    fun toDomain() : QuestionModel{
       return QuestionModel(id = id ?: "No hay id",
                            title = title ?:  "No se ha podido cargar la pregunta",
                            type = type ?: "Tipo de pregunta no clasificada")
    }
}