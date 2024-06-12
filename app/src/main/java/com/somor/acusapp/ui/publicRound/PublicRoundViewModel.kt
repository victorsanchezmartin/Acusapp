package com.somor.acusapp.ui.publicRound

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.somor.acusapp.data.network.FirebaseService
import com.somor.acusapp.domain.QuestionModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublicRoundViewModel @Inject constructor(private val firebaseService: FirebaseService): ViewModel() {

    private val _questionList = MutableStateFlow<List<QuestionModel>>(emptyList())
    val questionList : StateFlow<List<QuestionModel>> = _questionList

    private val _runningOrder = MutableLiveData(0)
    val runningOrder: LiveData<Int> = _runningOrder

    private val _isLoadedList = MutableLiveData<Boolean>()
    val isLoadedList :LiveData<Boolean> = _isLoadedList

    private val _previousButtonEnabled = mutableStateOf(false)
    val previousButtonEnabled : State<Boolean> = _previousButtonEnabled

    private val _showDialog = mutableStateOf(false)
    val showDialog : State<Boolean> = _showDialog

    private val _onBackEffect =  mutableStateOf(false)
    val onBackEffect : State<Boolean> = _onBackEffect

    fun onNextQuestion(){
        _onBackEffect.value = false
        _runningOrder.value = _runningOrder.value!! + 1
        _previousButtonEnabled.value = true
    }
    fun onPreviousQuestion(){
            _runningOrder.value = _runningOrder.value!! - 1
            if(_runningOrder.value == 0) _previousButtonEnabled.value = false
        _onBackEffect.value = true
    }

    fun getQuestions() {
        viewModelScope.launch {
        firebaseService.getQuestions().collect() {
            _questionList.value= it.shuffled() //Barajo la lista solo la primera vez que entramos a jugar
            _isLoadedList.value = true
            }
        }
    }

    fun onBackPressed() {
        _showDialog.value = true
    }

    fun onDismissDialog() {
        _showDialog.value = false

    }

    fun onConfirmation(navigateToHomeScreen: () -> Unit) {
        _showDialog.value = false
        viewModelScope.cancel()
        navigateToHomeScreen()
    }

}