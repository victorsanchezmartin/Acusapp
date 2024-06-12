package com.somor.acusapp.data.network.response

import com.somor.acusapp.domain.PlayerModel

data class PlayerResponse (
    val playerId: String? = null,
    val playerName: String? = null,
    val owner: Boolean? = null,
    val playerIUState: String? = null,
    val selected: Boolean ?= null
    ){


    fun toDomain(): PlayerModel{
        return PlayerModel(
            playerId = playerId ?: "no se encontró jugador",
            playerName = playerName?: "No se encontró el nombre",
            owner = owner ?: false,
            playerIUState = playerIUState ?: "no hay estado",
            selected = selected ?: false)
    }


}