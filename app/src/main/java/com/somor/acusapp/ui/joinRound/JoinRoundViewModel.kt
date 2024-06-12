package com.somor.acusapp.ui.joinRound

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.somor.acusapp.data.network.FirebaseService
import com.somor.acusapp.domain.PlayerModel
import com.somor.acusapp.domain.RoundModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinRoundViewModel @Inject constructor(private val firebaseService : FirebaseService, application: Application) : ViewModel() {
    private val context: Context = application.applicationContext

    private val _roundList = MutableStateFlow<List<RoundModel>>(emptyList())
    val roundList : StateFlow<List<RoundModel>> = _roundList

    private val _idRound = MutableLiveData("")
    val idRound: LiveData<String> =  _idRound

    private val _buttonEnabled = MutableLiveData<Boolean>()
    val buttonEnabled: LiveData<Boolean> =  _buttonEnabled

    private val _isSelectedRound = MutableLiveData<Boolean>()

    private val _playerList = MutableStateFlow(emptyList<PlayerModel>().toMutableList())
    val playerList : StateFlow<MutableList<PlayerModel>> = _playerList

    private val _playerNameSelected = MutableStateFlow("")
    val playerNameSelected : StateFlow<String> = _playerNameSelected

    private var _isGameReady = MutableStateFlow(false)
    private val _player = MutableStateFlow(PlayerModel("","",false,"", false))
            val player:StateFlow<PlayerModel> = _player


    /*Esta función cambia el valor de enabled del botón mediante la comprobación de si están seleccionados jugador y ronda */
    private fun checkTextFields() {
       _buttonEnabled.value =  _playerNameSelected.value.isNotEmpty() && _isSelectedRound.value == true && _isGameReady.value
    }

    private fun checkSelectedPlayer() {
        _playerList.value.map {
            if(_playerNameSelected.value == it.playerName && it.selected){
                _buttonEnabled.value = false
            }
        }
    }

    fun onButtonClick(idRound: String, navigateToAnonymousRound:(String, String, Boolean)-> Unit, ) {

        val copy = _player.value.copy(selected = true) //CAMBIAR ESTO POR TRUE
        firebaseService.setTrueSelectedPlayer(_idRound.value!!, copy)
        navigateToAnonymousRound(idRound, copy.playerId, copy.owner)}

    fun getRounds() {
        viewModelScope.launch {
            firebaseService.getRounds().collect(){
                _roundList.value = it
            }
        }
    }

    fun onSelectedRound(idRound : String) {
        _idRound.value = idRound
        _isSelectedRound.value = true
        _playerNameSelected.value = ""
        checkTextFields()
        isGameReady() //Cada vez que seleccione una partida ya tengo asignado un id, con lo que puedo lanzar el flow para ver cuándo está el game Ready
    }

    fun getPlayerList(idRound: String) {
        viewModelScope.launch {
            firebaseService.getPlayers(idRound).collect{//Cada vez que hay un cambio en FB se llama al collect
               _playerList.value = it.toMutableList()
                //Cada vez que haya cambios compruebo si el botón de unirse puede habilitarse
                checkSelectedPlayer()
            }
        }
    }

    fun onPlayerSelected(playerModel: PlayerModel) {
        _playerNameSelected.value = playerModel.playerName
        _player.value = playerModel
        checkTextFields()
    }

    private fun isGameReady(){
            viewModelScope.launch {
                firebaseService.isRoundStarted(_idRound.value!!).collect{
                    _isGameReady.value = it!!.roundStarted
                    checkTextFields()
                }
            }
    }
}








