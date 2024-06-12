package com.somor.acusapp.data.network.response

import com.somor.acusapp.domain.QuestionAnsweredModel

data class QuestionAnsweredResponse (
    val accuserId : String ? = null,
    val accusedName : String ? = null)
{
    fun toDomain() : QuestionAnsweredModel{
        return QuestionAnsweredModel(
            accuserId = accuserId ?: "No se ha encontrado acusador",
            accusedName = accusedName ?: "No se ha encontrado acusado"
        )
    }
}
