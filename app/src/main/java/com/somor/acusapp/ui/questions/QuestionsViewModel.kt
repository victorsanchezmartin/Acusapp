package com.somor.acusapp.ui.questions

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.somor.acusapp.data.network.FirebaseService
import com.somor.acusapp.data.network.dto.QuestionDto
import com.somor.acusapp.domain.QuestionModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionsViewModel @Inject constructor(private val firebaseService : FirebaseService): ViewModel(){

    private val _questionList = MutableStateFlow<List<QuestionModel>>(emptyList())
    val questionList : StateFlow<List<QuestionModel>> = _questionList


    //Controlo los estados del Screen desde aquí
    private val _questionTitle = MutableLiveData<String>()
    val questionTitle : LiveData<String> = _questionTitle


    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean> = _showDialog

    private val _questionId = MutableLiveData<String>()
    val questionId : LiveData<String> = _questionId

    private val _questionType= MutableLiveData<String>()
    val questionType : MutableLiveData<String> = _questionType


    private val _isAddQuestion = MutableLiveData<Boolean>()
    val isAddQuestion: LiveData<Boolean> = _isAddQuestion

    private val _isQuestionListLoading = MutableLiveData<Boolean>()
    val isQuestionListLoading: LiveData<Boolean> = _isQuestionListLoading

    suspend fun getQuestions(){
        viewModelScope.launch {
            firebaseService.getQuestions().collect(){
                _questionList.value = it
            }
        }
    }

    fun addQuestion(titleQuestion : String, type : String) {
        firebaseService.addQuestion(titleQuestion, type)
        onDismissDialog()
    }

    /*
    Primero busco la Id en RTDB y me la traigo convertido a model
    A continuación, hago una copia y le modifico el valor que quiero
    Finalmente, guardo esa copia
     */
    fun editQuestion(id: String, title : String, type : String) {

        firebaseService.editQuestion (QuestionDto(id, title, type))
        onDismissDialog()
    }

    //Recibo un id y busco su titulo y categoría
    fun onClickQuestion(question : QuestionModel){
        _questionTitle.value = question.title
        _questionType.value = question.type
        _questionId.value = question.id
        _isAddQuestion.value = false
        _showDialog.value = true //Abro el dialogo aquí una vez que ya tenga toda la información de la pregunta seleccionada
    }

    fun onCLickAddQuestion() {
        _isAddQuestion.value = true
        _showDialog.value = true
    }


    //Aqui manejo el campo de texto cuando escribimos en el text Field
    fun onTextFieldQuestionChanged(it: String) {
        _questionTitle.value = it
    }

    fun onDismissDialog() {
        _showDialog.value = false
        _questionType.value = ""
        _questionId.value = ""
        _questionTitle.value = ""
    }

    fun onDeleteQuestion(questionId: String) {
            firebaseService.deleteQuestion(questionId)
        onDismissDialog()
    }

    fun onImageSelected(type: String) {
        _questionType.value = type

    }
}