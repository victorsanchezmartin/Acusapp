package com.somor.acusapp.ui.anonymousRound

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.somor.acusapp.data.network.FirebaseService
import com.somor.acusapp.domain.PlayerModel
import com.somor.acusapp.domain.QuestionModel
import com.somor.acusapp.ui.anonymousRound.UIStates.AnonymousRoundUIState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch


@HiltViewModel(assistedFactory = AnonymousRoundViewModel.Factory::class)
class AnonymousRoundViewModel @AssistedInject constructor (
    @Assisted ("roundId") val roundId: String,
    @Assisted ("playerId")val playerId : String ,
    @Assisted ("owner")val owner: Boolean ,

    private val firebaseService : FirebaseService) : ViewModel (),
    DefaultLifecycleObserver {
    @AssistedFactory interface Factory {
        fun create( @Assisted ("roundId")  roundId: String,
                    @Assisted ("playerId") playerId : String ,
                    @Assisted ("owner") owner: Boolean) : AnonymousRoundViewModel
        }

    companion object {
        private const val LOADING = "Loading"
        private const val WAITING = "Waiting"
        private const val QUESTION = "Question"
        private const val ANSWER = "Answer"
        private const val END = "End"
    }
     private val _accusedList = MutableStateFlow(emptyList<String>().toMutableList())
    val accusedList: StateFlow<MutableList<String>> = _accusedList

     private val _mostAccusedList = MutableStateFlow(emptyList<String>().toMutableList())
    val mostAccusedList : StateFlow<MutableList<String>> = _mostAccusedList

    private val _mostAccusedPlayer = MutableStateFlow("")
    val mostAccusedPlayer : StateFlow<String> = _mostAccusedPlayer


    private val _iuState = MutableStateFlow<AnonymousRoundUIState>(AnonymousRoundUIState.Loading)
    val iuState: StateFlow<AnonymousRoundUIState> = _iuState

    private val _questionList = MutableStateFlow<List<QuestionModel>>(emptyList())
    val questionList: StateFlow<List<QuestionModel>> = _questionList

    private val _roundStarted = MutableStateFlow(false)
    val roundStarted: StateFlow<Boolean> = _roundStarted

    private val _playerList = MutableStateFlow<List<PlayerModel>>(emptyList())//De momento lo dejo asi por si algun jugador abandona
    val playerList: StateFlow<List<PlayerModel>> = _playerList

    private val _showDialog = mutableStateOf(false)
    val showDialog: MutableState<Boolean> = _showDialog

    private lateinit var _roundId : String

    private val _playerSelected = mutableStateOf(PlayerModel("", "", false, "", false))
    val playerSelected : State<PlayerModel> = _playerSelected




    private val _accuseButtonEnabled = MutableStateFlow(false)
    val accuseButtonEnabled: StateFlow<Boolean> = _accuseButtonEnabled


    private var _questionId = ""

    private var _runningOrder :Int = 0


     private val _questionTitle = MutableStateFlow("")
    val questionTitle :StateFlow<String> = _questionTitle

    var _playerId : String

    private var _owner : Boolean = false

   init {
        Log.d("collect listaScreen", "getIUStateAntes")
        _playerId = playerId
        _roundId = roundId
        _owner = owner
       Log.d("datos jugador", "jugador: $_playerId; ronda:  $_roundId; propietario: $_owner")

        viewModelScope.launch {
            firebaseService.getIUState(roundId, playerId).collect {
                when (it?.playerIUState) {   //Segun cambie de estado en RT actualizo el estado de la Screen
                    LOADING -> {
                        loading(roundId,playerId,owner)
                        _iuState.value = AnonymousRoundUIState.Loading
                    }
                    WAITING -> {
                        Log.d("collect listaScreen", "State Waiting")
                        _iuState.value = AnonymousRoundUIState.Waiting
                    }
                    QUESTION -> {
                        Log.d("collect listaScreen", "QuestionLoading")
                        questionState()
                        //La Screen la actualizo cuando haya conseguido la pregunta
                    }
                    ANSWER -> {
                        _iuState.value = AnonymousRoundUIState.Answer
                    }END -> {
                    _iuState.value = AnonymousRoundUIState.End
                }
                }
            }
            Log.d("collect listaScreen", "getIUStateDEspues")
        }
    }

    private fun loading(roundId: String, playerId: String, owner: Boolean?) {
        getPlayerList(_roundId)
        if (owner == true) {
            Log.d("collect listaScreen", "PASAAA PORR AQUIII")
            getQuestions(roundId)
        } else {
            //isRoundStarted(roundId)
            if(_playerList.value.isNotEmpty())
                Log.d("ruteision2", "pasandooo")
            firebaseService.setIUState(QUESTION, roundId, _playerId)
        }
    }

    private fun questionState() {

        getPlayerList(_roundId)
            Log.d("collect lista", "getQuestionAntes: ${_questionTitle.value}")
           getQuestionPLaying()
            Log.d("collect lista", "getQuestionDespues: ${_questionTitle.value}")
    }

    fun getQuestionPLaying() {
        viewModelScope.launch {
            firebaseService.getQuestionPlaying(_roundId).take(1).collect {
                _questionId = it.idQuestionPlaying
                Log.d("collect lista", "getQuestionDurante: $_questionId")

                findById(it.idQuestionPlaying)
                // _questionPlaying.value = it.idQuestionPlaying
            }
        }
    }

    //solo owner.
     fun getQuestions(roundId: String) {
        if (_questionList.value.isEmpty()) {
            Log.d("collect lista", "getQuestionsAntes()")
            viewModelScope.launch {
                firebaseService.getQuestions().take(1).collect {
                    if (_questionList.value.isEmpty()) {
                        _questionList.value = it.shuffled() //Barajo la lista solo la primera vez que entramos a jugar
                        Log.d(
                            "collect lista",
                            "getQuestionsDurante().Pregunta: ${_questionList.value[0].title}"
                        )
                        //Cuando acabe de rellenar la lista es el momento poner en FB cuál es la primera pregunta
                    }
                    setQuestionPlaying(roundId)
                    firebaseService.setRoundStartedTrue(roundId)
                    Log.d("collect lista", "getQuestionsDespues()")
                    firebaseService.setIUState(QUESTION,_roundId, _playerId)
                }
            }
        }
    }

    //solo owner
    private fun setQuestionPlaying(roundId: String) {

         if (_questionList.value.isNotEmpty()){
             Log.d("collect lista", "set question playing: id: ${_questionList.value[_runningOrder].id} ")
             firebaseService.setQuestionPlaying(roundId, _questionList.value[_runningOrder].id)

        }
    }

    fun getPlayerList(roundId: String) {
        if(_playerList.value.isEmpty()){
            Log.d("collect lista", "getPlayerListAntes()")
            viewModelScope.launch {
                firebaseService.getPlayers(roundId).collect {
                    Log.d("collect lista", "getPlayerListDurante()..it size: ${it.size}")
                    Log.d("collect lista", "getPlayerListDurante().._questlist.value size: ${_playerList.value.size}")
                    if(_playerList.value.size != it.size ){
                        _playerList.value = it
                        Log.d("collect lista", "getPlayerListDurante()...tamaño: ${_playerList.value.size}")
                    }
                }
            }
            Log.d("collect lista", "getPlayerListDespues()")
        }
    }

    fun setFalseSelectedPlayer(
        roundId: String,
        playerId: String,
        navigateToHomeScreen: () -> Unit
    ) {
            _showDialog.value = false
            //firebaseService.setIUState(LOADING, roundId, playerId)
            firebaseService.setFalseSelectedPlayer(roundId, playerId)
        viewModelScope.cancel() //Cuando me salgo de esta screen cancelo el view Model para que no se quede abierto
            navigateToHomeScreen()
        //Tengo que navegar a la pantalla anterior
    }

    fun onDisMissDialog() {
        _showDialog.value = false
    }

    fun onShowDialog() {
        _showDialog.value = true
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
    }

    fun onNextQuestion() {
        _accusedList.value.clear()
       // _questionTitle.value = ""
        Log.d("collect listaONNEXT", "ordenAntes: $_runningOrder")
        _runningOrder++
        Log.d("collect listaONNEXT", "ordenDespues: $_runningOrder")
        if(_runningOrder != _questionList.value.size){
                    setQuestionPlaying(_roundId)
                    setIUAllPlayers(_roundId, QUESTION)
        }else{
                    setIUAllPlayers(_roundId, END) //Esto hay que ponerlo en todos los jugadores
                    _iuState.value = AnonymousRoundUIState.End
    }
    }

    private fun setIUAllPlayers(_idRound: String, iuState: String) {
        for(items in _playerList.value){
            firebaseService.setIUState(iuState, _idRound, items.playerId ) //Esto hay que ponerlo en todos los jugadores
        }
    }

    fun onPlayerSelected(player: PlayerModel) {
        _playerSelected.value = player
        _accuseButtonEnabled.value = true
        //_accuseButtonEnabled = true
    }

    fun onAccuseButton2(playerId: String) {
            firebaseService.addAccused(_playerSelected.value.playerName, _roundId, _questionId , playerId)
        Log.d("collect listaGetAccPl2", "onAccusedButt2")
            //firebaseService.setIUState(WAITING, _roundId, _playerId) //Actualizo RT
           // _iuState.value = AnonymousRoundUIState.Waiting //Actualizo Screen

            getListAccusedPlayers2(playerId)
    }

     fun getListAccusedPlayers2(playerId: String) {
        Log.d("collect listaGetAccPl2","getListAccPlay2")
        viewModelScope.launch {

            Log.d("collect listaGetAccPl2", "La pregunta es: $_questionId")
            if (_questionId.isEmpty()) {
                _iuState.value = AnonymousRoundUIState.Loading
            } else {
                firebaseService.getAccusedList2(_roundId, _questionId).collect {
                    _accusedList.value = it as MutableList<String>
                    Log.d("collect listaDuranteGetPLa", "getListAccPlay2Durante")
                    if (_accusedList.value.isNotEmpty())
                        if (_accusedList.value.size == _playerList.value.size) {
                            setAccuseButtonEnabledFalse()
                            firebaseService.setIUState(ANSWER, _roundId, playerId)
                            // _iuState.value = AnonymousRoundUIState.Answer
                        } else {
                            firebaseService.setIUState(WAITING, _roundId, playerId)
                            //_iuState.value = AnonymousRoundUIState.Waiting
                        }
                }
            }
        }
    }

    private fun findById(questionId: String) {
        viewModelScope.launch {
            firebaseService.findQuestionById(questionId).take(1).collect{
                if (it != null) {
                    _questionTitle.value = it.title
                        if( _iuState.value != AnonymousRoundUIState.Waiting)  {
                            _iuState.value = AnonymousRoundUIState.Question
                        }
                    Log.d("collect lista", "findById: ${_questionTitle.value}")
                    //Una vez encontrada la ppregunta,actualizo la Screen
                }
            }
        }
    }

    fun getMostAccused() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("collect listaArriba0","getMostAccused")
                _mostAccusedList.value.clear()//Antes de cada recomosicion vacio la lista
            var flowList: MutableList<String> = emptyList<String>().toMutableList() //creo esta lista auxiliar para igualarla a _mostAccusedlist y que cause la recomposicion
            firebaseService.getMostAccused(_roundId).take(1).collect{
                Log.d("collect listaArriba11",it.toString())

                it.map { list ->
                    Log.d("collect listaAbajo12",list.toString())
                    list.map {name->
                        if (name != null) {
                            Log.d("collect listaAbajo13",name)
                            flowList.add(name)
                        }
                    }
                }
                Log.d("collect listaAbajo14",_mostAccusedList.value.toString())
                _mostAccusedList.value = flowList
            }
        }
    }

    fun setAccuseButtonEnabledFalse() {
        Log.d("collect listaAbajo14","valor del boton: $_accuseButtonEnabled")
        _accuseButtonEnabled.value = false
        _playerSelected.value = PlayerModel("", "", false, "", false)

    }
}


