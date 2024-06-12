package com.somor.acusapp.domain

import com.somor.acusapp.data.network.dto.PlayerDto

data class PlayerModel(val playerId: String, val playerName: String, val owner: Boolean, val playerIUState: String, val selected: Boolean){
    fun toData() : PlayerDto{
        return PlayerDto(playerId = playerId,
        playerName = playerName,
        owner = owner,
            playerIUState = playerIUState,
            selected = selected)
    }
}