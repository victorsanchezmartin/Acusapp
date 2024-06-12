package com.somor.acusapp.ui.questions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.somor.acusapp.R
import com.somor.acusapp.domain.QuestionModel


@Composable
fun QuestionDialog(
    isAddQuestion: Boolean,
    questionId: String,
    questionTitle: String,
    questionViewModel: QuestionsViewModel,
    questionType: String
) {

    Dialog(onDismissRequest = { questionViewModel.onDismissDialog()}) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var titleDialog by remember { mutableStateOf("") }
                        if(isAddQuestion) titleDialog = "Añade la pregunta" else titleDialog="Modifica la pregunta"
                        Text(
                            text = titleDialog,style = TextStyle(fontSize = 24.sp,fontFamily = FontFamily.Default,fontWeight = FontWeight.Bold)
                        )
                        Icon(
                            imageVector = Icons.Filled.Close,contentDescription = "",tint = colorResource(android.R.color.darker_gray),modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                                .clickable { questionViewModel.onDismissDialog() })
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            ,
                        value = questionTitle,
                        placeholder = { Text(text = "Introduce la pregunta") },
                        onValueChange = {
                            questionViewModel.onTextFieldQuestionChanged(it)})

                    Spacer(modifier = Modifier.height(20.dp))

                    ImageCard ({questionViewModel.onImageSelected(it)}, questionType , isAddQuestion)
                    Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                        Button(
                            onClick = {
                                if (questionTitle.isEmpty()) {
                                    return@Button
                                }
                                if(isAddQuestion)questionViewModel.addQuestion(questionTitle, questionType) else questionViewModel.editQuestion(questionId,questionTitle, questionType )
                            },
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(text = "Hecho")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ImageCard(onImageSelected:(String) -> Unit, questionType: String, isAddQuestion: Boolean) {

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        var borderMild: BorderStroke? = null
        var boderSpicy: BorderStroke? = null
        if (!isAddQuestion) {
            if (questionType == QuestionModel.MILD_TYPE) {
                borderMild = BorderStroke(3.dp, colorResource(id = R.color.custom_pink))
                boderSpicy = null
            } else {
                borderMild = null
                boderSpicy = BorderStroke(3.dp, colorResource(id = R.color.custom_pink))
            }
        }
            Card(
                modifier = Modifier.width(130.dp).clickable {
                    onImageSelected(QuestionModel.MILD_TYPE)
                },
                shape = RoundedCornerShape(15.dp),
                elevation = CardDefaults.cardElevation(20.dp),
                border = borderMild

            ) {
                Box(modifier = Modifier.height(80.dp)) {
                    Image(
                        painterResource(id = R.drawable.suave),
                        contentDescription = "guindillas suaves",
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp), contentAlignment = Alignment.BottomCenter
                    ) {
                        Text(
                            text = QuestionModel.MILD_TYPE, style = LocalTextStyle.current.merge(
                                TextStyle(
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    drawStyle = Stroke(width = 2f, join = StrokeJoin.Round)
                                )
                            )
                        )
                    }
                }
            }
            Card(
                modifier = Modifier.width(130.dp).clickable {
                    onImageSelected(QuestionModel.SPICY_TYPE)
                },
                shape = RoundedCornerShape(15.dp),
                elevation = CardDefaults.cardElevation(20.dp),
                border = boderSpicy
            ) {
                Box(modifier = Modifier.height(80.dp)) {
                    Image(
                        painterResource(id = R.drawable.picante),
                        contentDescription = "guindillas picantes",
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp), contentAlignment = Alignment.BottomCenter
                    ) {
                        Text(
                            text = QuestionModel.SPICY_TYPE,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                }

            }
        }
    }


