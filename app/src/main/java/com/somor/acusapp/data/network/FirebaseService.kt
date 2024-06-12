package com.somor.acusapp.data.network

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.snapshots
import com.somor.acusapp.data.network.dto.PlayerDto
import com.somor.acusapp.data.network.dto.QuestionAnsweredDto
import com.somor.acusapp.data.network.dto.QuestionDto
import com.somor.acusapp.data.network.dto.RoundDto
import com.somor.acusapp.data.network.response.PlayerResponse
import com.somor.acusapp.data.network.response.QuestionResponse
import com.somor.acusapp.data.network.response.RoundResponse
import com.somor.acusapp.domain.PlayerModel
import com.somor.acusapp.domain.QuestionModel
import com.somor.acusapp.domain.RoundModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject.Inject


class FirebaseService @Inject constructor(private val reference: DatabaseReference) {

    companion object {
        private const val PATH_QUESTIONS = "Questions"
        private const val PATH_ROUND = "Rounds"
        private const val PATH_PLAYERS_ROUND = "Players"
        private const val PATH_QUESTIONS_ANSWERED = "QuestionsAnswered"
    }

    fun getQuestions(): Flow<List<QuestionModel>> {
        val reference = reference.child(PATH_QUESTIONS)
        return reference.snapshots.map { dataSnapshot -> //Accedo a cada registro con su id general y sus atributos por debajo
            dataSnapshot.children.map {
                it //Accedo solo a los atributos
                it.getValue(QuestionResponse::class.java)?.toDomain()!!

            }
        }
    }

    fun addQuestion(titleQuestion: String, type: String) {
        val reference = reference.child(PATH_QUESTIONS).push()
        val key = reference.key ?: null
        reference.setValue(QuestionDto(key, titleQuestion, type))
    }

    fun editQuestion(question: QuestionDto?) {
        reference.child("$PATH_QUESTIONS/${question?.id}").setValue(question)
    }

    fun deleteQuestion(questionId: String) {
        reference.child("$PATH_QUESTIONS/$questionId").removeValue()
    }

    fun insertNewRound(): RoundDto {
        val reference = reference.child(PATH_ROUND).push()
        val idRound = reference.key

        val round = createNewRound(idRound!!)
        reference.setValue(round)
        return round
    }

    private fun createNewRound(idRound: String): RoundDto {
        val playerList = listOf<PlayerDto>()
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currentDate = formatter.format(time)
        val listQuestionsAnswered = listOf<QuestionAnsweredDto>()
        return RoundDto(
            idRound,
            playerList,
            false,
            currentDate,
            "",
            listQuestionsAnswered
        ) //Cuando creamos la ronda, ponemos a vacío el
        //identificador de la pregunta , todavía no ha empezado el juego
    }

    /* Esta funcion es llamada solo por el jugador que crea la partida.
    Insertamos al creador de la partida en FB y en firebase tenemos que cambiar el valor de roundStarted*/
    fun startNewRound(
        playerId: String?,
        playerName: String?,
        roundId: String,
        isOwner: Boolean,
        iuState: String
    ) {
        //val idPlayer =  insertNewPlayer(playerName, roundId, isOwner, iuState)
        reference.child("$PATH_ROUND/$roundId/roundStarted").setValue(true)
        // setIUStateQuestion(roundId, idPlayer)
        /* getPlayers(roundId).collect(){
            it[0].playerIUState
            Log.d("jugadorcollec", it[0].playerIUState)
        }

        */
        updateIU(playerId!!, playerName!!, roundId, isOwner, iuState)
    }

    private fun updateIU(
        playerId: String,
        playerName: String,
        roundId: String,
        isOwner: Boolean,
        iuState: String
    ) {
        //val playerData = mapOf<String, String>("playerName" to playerName, "playerIUState" to iuState)

        val update = mapOf("playerIUState" to "Questione")
        reference.child(PATH_ROUND).child(roundId).child(PATH_PLAYERS_ROUND).child(playerId)
            .updateChildren(update).addOnSuccessListener {
                Log.d("modificado", "modificado")

            }.addOnFailureListener {
                Log.d("modificado", " no ha sido modificado")

            }

    }

    fun getPlayers(roundId: String): Flow<List<PlayerModel>> {
        val reference = reference.child(PATH_ROUND).child(roundId).child(PATH_PLAYERS_ROUND)
        return reference.snapshots.map { players ->
            players.children.map {

                it.getValue(PlayerResponse::class.java)?.toDomain()!!
            }
        }
    }


    private fun setIUStateQuestion(roundId: String, idPlayer: String) {
        /* val ref =  reference.child("$PATH_ROUND/$roundId/$PATH_PLAYERS_ROUND/$idPlayer/playerIUState")
            .setValue("Question") */

        val ref = reference.child(PATH_ROUND).child(roundId).child(PATH_PLAYERS_ROUND)
        Log.d("jugador", ref.toString())

        ref.snapshots.map { player ->
            Log.d("jugador", player.toString())

            player.children.map {
                Log.d("jugador", "iu")

                val player = it.getValue(PlayerResponse::class.java)!!.toDomain()
            }
        }

    }

    fun getIUState(roundId: String, playerId: String): Flow<PlayerModel?> {
        val reference = reference.child("$PATH_ROUND/$roundId/$PATH_PLAYERS_ROUND/$playerId")
        return reference.snapshots.map {
            it.getValue(PlayerResponse::class.java)?.toDomain()
        }


    }

    fun insertNewPlayer(
        playerName: String?,
        roundId: String,
        isOwner: Boolean,
        iuState: String,
        isSelected: Boolean
    ): String {
        val reference = reference.child("$PATH_ROUND/$roundId/$PATH_PLAYERS_ROUND").push()
        val idPlayer = reference.key //Establezco que la id del jugador sea la que genera firebase

        reference.setValue(PlayerDto(idPlayer!!, playerName!!, isOwner, iuState, isSelected))
        return idPlayer
    }

    fun getRounds(): Flow<List<RoundModel>> {
        return reference.child("$PATH_ROUND").snapshots.map { round ->
            round.children.map {
                it.getValue(RoundResponse::class.java)?.toDomain()!!
            }
        }
    }

    /* Recibo un identificador de ronda y un playerDTO. Me posiciono en la ruta del id del jugador y lo inserto con el campo playerIUState modificado (ya viene hecho).*/
    fun modifyOwner(roundId: String, playerDto: PlayerDto) {
        val reference =
            reference.child("$PATH_ROUND/$roundId/$PATH_PLAYERS_ROUND/${playerDto.playerId}")
        reference.setValue(playerDto)

    }

    fun setTrueSelectedPlayer(roundId: String, copy: PlayerModel) {
        val reference = reference.child("$PATH_ROUND/$roundId/$PATH_PLAYERS_ROUND/${copy.playerId}")
        reference.setValue(copy)
    }

    fun setFalseSelectedPlayer(roundId: String, playerId: String) {
        val reference =
            reference.child("$PATH_ROUND/$roundId/$PATH_PLAYERS_ROUND/$playerId/selected")
        reference.setValue(false)
    }

    fun setRoundStartedTrue(roundId: String) {
        val reference = reference.child("$PATH_ROUND/$roundId/roundStarted")
        reference.setValue(true)
    }

    fun isRoundStarted(roundId: String): Flow<RoundModel?> {
        val reference = reference.child("$PATH_ROUND/$roundId")
        return reference.snapshots.map {
            it.getValue(RoundResponse::class.java)?.toDomain()
        }
    }

    fun setQuestionPlaying(roundId: String, questionID: String) {
        val reference = reference.child("$PATH_ROUND/$roundId/idQuestionPlaying")
        reference.setValue(questionID)
    }

    fun getQuestionPlaying(roundId: String): Flow<RoundModel> {
        return reference.child("$PATH_ROUND/$roundId").snapshots.map {
            it.getValue(RoundResponse::class.java)?.toDomain()!!
        }
    }

    fun findQuestionById(idQuestion: String): Flow<QuestionModel?> {
        val ref = reference.child("$PATH_QUESTIONS/$idQuestion")
        return ref.snapshots.map {
            it.getValue(QuestionResponse::class.java)?.toDomain()
        }
    }

    /*
    fun onAccuseButton (
        list: List<String>,
        roundId: String,
        question: QuestionModel
    ) {
        val ref =reference.child("$PATH_ROUND/$roundId/$PATH_QUESTIONS_ANSWERED/${question.id}")
        ref.setValue(QuestionAnsweredDto(list))
    }


     */
    /*
    fun getAccusedList(roundId: String, question: QuestionModel): Flow<QuestionAnsweredModel?> {
            val refere =  reference.child("$PATH_ROUND/$roundId/$PATH_QUESTIONS_ANSWERED/${question.id}")
                return refere.snapshots.map {
                    it.getValue(QuestionAnsweredResponse::class.java)?.toDomain()
            }
    }

     */
    fun setIUState(state: String, roundId: String, playerId: String) {
        val reference = reference.child("$PATH_ROUND/$roundId/$PATH_PLAYERS_ROUND/$playerId/playerIUState")
        reference.setValue(state)
        Log.d("ruteision", "$PATH_ROUND/$roundId/$PATH_PLAYERS_ROUND/$playerId/playerIUState: $state")

    }

    fun addAccused(accused: String, roundId: String, questionId: String, accuser: String) {
        val ref = reference.child("$PATH_ROUND/$roundId/$PATH_QUESTIONS_ANSWERED/$questionId/$accuser")
        Log.d("ruta", "$PATH_ROUND/$roundId/$PATH_QUESTIONS_ANSWERED/$questionId/$accuser".toString())

        ref.setValue(accused)
    }

    fun getAccusedList2(roundId: String, questionId: String): Flow<List<String?>> {
        Log.d("collect listaGetAccList2", "FIREbase: getAccusedLis2")
        Log.d("collect listaGetAccList2", "FIREbase: la pregunta es : $questionId")

        val ref = reference.child("$PATH_ROUND/$roundId/$PATH_QUESTIONS_ANSWERED/${questionId}")

        return ref.snapshots.map { it ->
            Log.d("collect lista1", it.getValue().toString())

            it.children.map {qu ->
                Log.d("collect lista11", qu.getValue().toString())
                qu.getValue(String::class.java)
            }
        }

    }

    fun getMostAccused(roundId: String): Flow<List<List<String?>>> {
            return reference.child("$PATH_ROUND/$roundId/$PATH_QUESTIONS_ANSWERED").snapshots.map {it->
                it.children.map { it ->
                    Log.d("collect listaArriba1", it.value.toString())

                    it.children.map {name ->
                        Log.d("collect listaabajo1", name.value.toString())

                        name.getValue(String::class.java)

                    }
                }
            }
    }


}




