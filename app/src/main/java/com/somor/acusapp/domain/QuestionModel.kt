package com.somor.acusapp.domain

import com.somor.acusapp.data.network.dto.QuestionDto


class QuestionModel (
    val id : String,
    val title: String,
    val type: String)
{
    fun toDTO() : QuestionDto{
        return QuestionDto(
             id = id,
            title = title,
            type = type)
    }
    companion object {
         const val MILD_TYPE = "suave"
         const val SPICY_TYPE = "picante"
    }
}