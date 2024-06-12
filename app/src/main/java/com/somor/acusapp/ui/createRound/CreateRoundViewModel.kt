package com.somor.acusapp.ui.createRound

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.somor.acusapp.data.network.FirebaseService
import com.somor.acusapp.domain.PlayerModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateRoundViewModel @Inject constructor(private val firebaseService : FirebaseService): ViewModel() {


    private val _onTextChanged = MutableLiveData<String>()
    val onTextChanged: LiveData<String> = _onTextChanged

    private val _buttonAddPlayerEnabled = MutableLiveData<Boolean>()
    val buttonAddPlayerEnabled: LiveData<Boolean> = _buttonAddPlayerEnabled

    private val _playersList = MutableStateFlow<List<PlayerModel>>(emptyList())
    val playerList: StateFlow<List<PlayerModel>> = _playersList

    private var _playerNameList = mutableListOf<String>()

    private val _playerId = MutableLiveData<String>()

    private val _startButtonEnabled = MutableStateFlow(false)
    val startButtonEnabled: StateFlow<Boolean> = _startButtonEnabled

    private val _playerOwner = MutableStateFlow("")
    val playerOwner: StateFlow<String> = _playerOwner

    //Aqui he repetido variables
    private val _player = MutableStateFlow(PlayerModel("", "", false, "", false))

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    fun onTextChanged(it: String) {
        _onTextChanged.value = it
        //No empezaremos la partida si no hay nombre de jugador que crea la partida
        _buttonAddPlayerEnabled.value = it.isNotEmpty()
    }


    fun onStartRound(roundId: String, navigateToAnonymousRound: (String, String, Boolean) -> Unit) {
        val copy = _player.value.copy(owner = true, selected = true)
        firebaseService.setTrueSelectedPlayer(roundId, copy)

        navigateToAnonymousRound(roundId, _playerId.value!!, true)
    }

    fun insertPlayer(roundId: String, playerName: String) {
        viewModelScope.launch {
            if (_playerNameList.contains(playerName)) {
                _toastMessage.emit("No se pueden repetir nombres de jugadores")
        }else{
                _playerId.value = firebaseService.insertNewPlayer(playerName, roundId, false, "Loading", false)
            }
            onTextChanged("")
    }
}

    fun loadPlayerList(roundId: String) {
        viewModelScope.launch {
            firebaseService.getPlayers(roundId).collect{
                _playersList.value = it
                _playerNameList.clear()
                it.map {playerModel ->
                    _playerNameList.add(playerModel.playerName)
                }
            }
        }
    }
    fun onPlayerSelected(player: PlayerModel, roundId: String) {
        _playerOwner.value = player.playerName
        if(_playersList.value.size > 0){ //cambiar por 2
            _startButtonEnabled.value = true
        }
        _playerId.value = player.playerId
        _player.value = player


        /*Primero pongo todos a false y luego pongo a true el que ha sido seleccionado*/
        for (item in _playersList.value){
            val copy = item.copy(owner = false).toData()
            firebaseService.modifyOwner(roundId, copy)
        }
        firebaseService.modifyOwner(roundId, player.copy(owner = true).toData())

        }
    }



