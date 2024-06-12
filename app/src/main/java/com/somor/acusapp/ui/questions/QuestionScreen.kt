package com.somor.acusapp.ui.questions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.somor.acusapp.domain.QuestionCategory
import com.somor.acusapp.domain.QuestionModel
import com.somor.acusapp.ui.home.AutoResizedText
import com.somor.acusapp.ui.theme.MyMaroon
import com.somor.acusapp.ui.theme.NewRound
import com.somor.acusapp.ui.theme.PublicRound
import com.somor.acusapp.ui.theme.wenKaiFontFamily

@Composable
fun  QuestionScreen (
    questionViewModel: QuestionsViewModel = hiltViewModel(),

) {

    val showDialog by  questionViewModel.showDialog.observeAsState(initial = false)
    val isAddQuestion by  questionViewModel.isAddQuestion.observeAsState(initial = false)
    val questionId by  questionViewModel.questionId.observeAsState(initial = "")
    val questionTitle by questionViewModel.questionTitle.observeAsState(initial = "")
    val questionType by questionViewModel.questionType.observeAsState(initial = "")

    LaunchedEffect(true ){//Cargo una vez las preguntas
        questionViewModel.getQuestions()
    }

    if(showDialog) {
        QuestionDialog(
            isAddQuestion = isAddQuestion,
            questionId = questionId,
            questionTitle = questionTitle,
            questionType = questionType,
            questionViewModel = questionViewModel
        )
    }

    val listState = rememberLazyListState() //Uso este estado para mejorar el rendimiento
    val isExpanded by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } } //Controlo la expansión o contracción

    val questionList by questionViewModel.questionList.collectAsState()

    Scaffold(
        floatingActionButton = {
            AddQuestionFAB(isExpanded,
                onCLickAddQuestion = {questionViewModel.onCLickAddQuestion()})

        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .fillMaxSize()
            .background(
                MyMaroon
            )
        ){
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center){
                AutoResizedText(text = "Preguntas", style = MaterialTheme.typography.headlineLarge)

            }
            Box(modifier = Modifier.weight(8f)){

                QuestionsList( padding, listState, questionList,
                    onClickQuestion = {questionViewModel.onClickQuestion(it)} ,
                    onDeleteQuestion = {questionViewModel.onDeleteQuestion(it)})
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuestionsList(
    padding: PaddingValues,
    listState: LazyListState,
    questionList: List<QuestionModel>,
    onClickQuestion:(QuestionModel) -> Unit,
    onDeleteQuestion:(String) -> Unit
    ) {

    val questionListOrder = questionList.groupBy { it.type }.toSortedMap().map {
        QuestionCategory(name = it.key.toString(), items = it.value)
    }
    LazyColumn(
        modifier = Modifier.padding(padding),
        state = listState
    ) {
        questionListOrder.forEach { category ->
            stickyHeader {
                CategoryHeader(category.name)
            }
            items(category.items) { question ->
                CategoryItem(question, onClickQuestion = onClickQuestion, onDeleteQuestion = onDeleteQuestion )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoryItem(
    question: QuestionModel,
    onClickQuestion:(QuestionModel) -> Unit,
    onDeleteQuestion:(String) -> Unit
) {
    Text(
        text = question.title,
        fontSize = 18.sp,
        fontFamily = wenKaiFontFamily,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            //Aqui usa la propiedad combinedClickable que proporciona dos parámetros para gestionar los click. Tengo que declarar ambos aunque solo use uno.
            .combinedClickable(onLongClick = { onDeleteQuestion(question.id) },
                onClick = { onClickQuestion(question) }
            )
         )
}

@Composable
private fun CategoryHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    var background : Color = Color.Red

    if(text == QuestionModel.MILD_TYPE){
        background = PublicRound
    }else
        background
    // Aqui uso un box para poder darle el fondo correspondiente, ya que la funcion que uso para establecer la tipografia no tiene esta propiedad
    Box(modifier = Modifier.background(background). fillMaxWidth(). padding(16.dp)){
        AutoResizedText(
            text = text.uppercase(), style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Composable
fun AddQuestionFAB(expanded: Boolean, onCLickAddQuestion: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = {onCLickAddQuestion()},
        icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = "Add question") },
        text = { Text(text = "Añadir pregunta", fontSize =16.sp)},
        containerColor = NewRound,
        contentColor = Color.White,
        expanded = expanded
    )
}
