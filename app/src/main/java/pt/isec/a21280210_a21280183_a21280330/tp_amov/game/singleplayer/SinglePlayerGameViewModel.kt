package pt.isec.a21280210_a21280183_a21280330.tp_amov.game.singleplayer

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.isec.a21280210_a21280183_a21280330.tp_amov.data.GameState
import pt.isec.a21280210_a21280183_a21280330.tp_amov.data.Player
import pt.isec.a21280210_a21280183_a21280330.tp_amov.database.LocalReader
import pt.isec.a21280210_a21280183_a21280330.tp_amov.game.GameModel


class SinglePlayerGameViewModel : ViewModel() {

    private var firebase : FirebaseAuth = Firebase.auth
    private var db : FirebaseFirestore = Firebase.firestore
    val gameModel = GameModel
    var totalTime : Long = 0

    fun setupGame(){

        gameModel.gameState = MutableLiveData(GameState.PLAYING)
        gameModel.player = Player(firebase.currentUser?.uid.toString(),LocalReader.getName(),0,0)
        gameModel.player!!.avatar = BitmapFactory.decodeFile(LocalReader.getAvatarPath())
        gameModel.initializeLevel()
        gameModel.setupBoard()

    }

    fun makeMove(position: Int, type : String,context: Context){

        checkPlay(position,type,context)
        nextLevel()
    }

    private fun checkPlay(position: Int, type: String,context: Context){

        val row1 : Int = calculateRowOrColumn(
                            gameModel.board[0].toInt(),
                            gameModel.board[2].toInt(),
                            gameModel.board[4].toInt(),
                            gameModel.board[1],
                            gameModel.board[3])

        val row3 : Int = calculateRowOrColumn(
                            gameModel.board[10].toInt(),
                            gameModel.board[12].toInt(),
                            gameModel.board[14].toInt(),
                            gameModel.board[11],
                            gameModel.board[13])

        val row5 : Int = calculateRowOrColumn(
                            gameModel.board[20].toInt(),
                            gameModel.board[22].toInt(),
                            gameModel.board[24].toInt(),
                            gameModel.board[21],
                            gameModel.board[23])

        val column1 : Int = calculateRowOrColumn(
                            gameModel.board[0].toInt(),
                            gameModel.board[10].toInt(),
                            gameModel.board[20].toInt(),
                            gameModel.board[5],
                            gameModel.board[15])

        val column3 : Int = calculateRowOrColumn(
                            gameModel.board[2].toInt(),
                            gameModel.board[12].toInt(),
                            gameModel.board[22].toInt(),
                            gameModel.board[7],
                            gameModel.board[17])
        val column5 : Int = calculateRowOrColumn(
                            gameModel.board[4].toInt(),
                            gameModel.board[14].toInt(),
                            gameModel.board[24].toInt(),
                            gameModel.board[9],
                            gameModel.board[19])

        val results : ArrayList<Int> = ArrayList(5)
        results.addAll(mutableListOf(row1,row3,row5,column1,column3,column5))

        var first = 0
        var second = 0

        for(n in results){
            if(n > first){

                second = first
                first = n
            }
            if(n != first && n > second){
                second = n
            }
        }

        if(type == "row"){
            checkRow(position, first, second, row1, row3, row5, context)
        }
        else if(type == "column") {
            checkColumn(position, first, second, column1, column3, column5, context)
        }

    }

    private fun checkRow(position : Int, first : Int, second : Int, row1 : Int, row3 : Int, row5 : Int, context : Context){

        when(position){
            1 -> {
                when(row1){
                    first -> {
                        increaseTime()
                        gameModel.player!!.score += 2
                        gameModel.player!!.correctAnswers++
                        Toast.makeText(
                            context,
                            "Move on row [$position] -> Correct +2 points.",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    second -> {
                        increaseTime()
                        gameModel.player!!.score += 1
                        gameModel.player!!.correctAnswers++
                        Toast.makeText(
                            context,
                            "Move on row [$position] -> Correct +1 point.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else ->
                    {
                        Toast.makeText(
                            context,
                            "Move on row [$position] -> Incorrect.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            3 -> {
                when(row3) {
                    first -> {
                        increaseTime()
                        gameModel.player!!.score += 2
                        gameModel.player!!.correctAnswers++
                        Toast.makeText(
                            context,
                            "Move on row [$position] -> Correct +2 points.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    second -> {
                        increaseTime()
                        gameModel.player!!.score += 1
                        gameModel.player!!.correctAnswers++
                        Toast.makeText(
                            context,
                            "Move on row [$position] -> Correct +1 point.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> Toast.makeText(
                        context,
                        "Move on row [$position] -> Incorrect.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            5 -> {
                when(row5){
                    first -> {
                        increaseTime()
                        gameModel.player!!.score += 2
                        gameModel.player!!.correctAnswers++
                        Toast.makeText(
                            context,
                            "Move on row [$position] -> Correct +2 points.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    second -> {
                        increaseTime()
                        gameModel.player!!.score += 1
                        gameModel.player!!.correctAnswers++
                        Toast.makeText(
                            context,
                            "Move on row [$position] -> Correct +1 point.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> Toast.makeText(
                        context,
                        "Move on row [$position] -> Incorrect.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }

    private fun checkColumn(position : Int, first : Int, second : Int, column1 : Int, column3 : Int, column5 : Int, context : Context){

        when(position){
            1 -> {
                when(column1){
                    first -> {
                        increaseTime()
                        gameModel.player!!.score += 2
                        gameModel.player!!.correctAnswers++
                        Toast.makeText(
                            context,
                            "Move on column [$position] -> Correct +2 points.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    second -> {
                        increaseTime()
                        gameModel.player!!.score += 1
                        gameModel.player!!.correctAnswers++
                        Toast.makeText(
                            context,
                            "Move on column [$position] -> Correct +1 point.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else ->
                    {
                        Toast.makeText(
                            context,
                            "Move on column [$position] -> Incorrect.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            3 -> {
                when(column3) {
                    first -> {
                        increaseTime()
                        gameModel.player!!.score += 2
                        gameModel.player!!.correctAnswers++
                        Toast.makeText(
                            context,
                            "Move on column [$position] -> Correct +2 points.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    second -> {
                        increaseTime()
                        gameModel.player!!.score += 1
                        gameModel.player!!.correctAnswers++
                        Toast.makeText(
                            context,
                            "Move on column [$position] -> Correct +1 point.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> Toast.makeText(
                        context,
                        "Move on column [$position] -> Incorrect.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            5 -> {
                when(column5){
                    first -> {
                        increaseTime()
                        gameModel.player!!.score += 2
                        gameModel.player!!.correctAnswers++
                        Toast.makeText(
                            context,
                            "Move on column [$position] -> Correct +2 points.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    second -> {
                        increaseTime()
                        gameModel.player!!.score += 1
                        gameModel.player!!.correctAnswers++
                        Toast.makeText(
                            context,
                            "Move on column [$position] -> Correct +1 point.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> Toast.makeText(
                        context,
                        "Move on column [$position] -> Incorrect.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun nextLevel(){

        gameModel.level.countdown--

        if(gameModel.level.countdown == 0)
            checkCorrectAnswers()
        else
            gameModel.setupBoard()

    }

     fun checkCorrectAnswers(){

        if(gameModel.player!!.correctAnswers >= gameModel.level.minAnswers){

            if(gameModel.level.number == 4){
                gameModel.player!!.isWinner = true
                gameModel.gameState.postValue(GameState.FINISHED)
            }
            else{
                gameModel.setupLevel()
                gameModel.player!!.correctAnswers = 0
                gameModel.gameState.postValue(GameState.PAUSED)
                gameModel.setupBoard()
            }
        }
        else
            gameModel.gameState.postValue(GameState.FINISHED)


    }

    fun getBoard(): ArrayList<String> {
        return gameModel.board
    }

    private fun increaseTime(){

        if(gameModel.level.time + 5000 <= gameModel.level.totalTime){
            gameModel.level.time += 5000
        }
        else
            gameModel.level.time = gameModel.level.totalTime

        gameModel.gameState.postValue(GameState.CORRECT_ANSWER)
    }

    fun updateDataInFireStore() {

        val seconds = (totalTime / 1000 % 60)
        val minutes = (totalTime / 1000 / 60)

        val duration = String.format("%02d,%02d",
            minutes,seconds
        )

        val collection = db.collection("Scores")
        collection
            .whereEqualTo("playerId",firebase.currentUser?.uid)
            .orderBy("score", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener {
                Log.i(TAG, "updateDataInFireStore: Success")
                if (it.size() < 5) {
                    collection.document()
                        .set(
                            hashMapOf(
                                "playerId" to gameModel.player!!.id,
                                "player" to gameModel.player!!.name,
                                "score" to gameModel.player!!.score,
                                "time" to duration,
                            )
                        )
                }
                else{
                    if (it.documents[0].getLong("score")!! >= gameModel.player!!.score)
                        return@addOnSuccessListener

                    it.documents[0].reference.delete()
                    collection.document()
                        .set(hashMapOf(
                                    "playerId" to gameModel.player!!.id,
                                    "player" to gameModel.player!!.name,
                                    "score" to gameModel.player!!.score,
                                    "time" to duration,
                                )
                        )
                }
            }
            .addOnFailureListener { e ->
                Log.i(TAG, "updateDataInFireStore: ${e.message}")
            }
    }

    private fun calculateRowOrColumn(first : Int,second : Int, third : Int, operator1 : String, operator2:String) : Int{

        // Addition first
        if(operator1 == "+" && operator2 == "+"){
            return first + second + third
        }
        else if(operator1 == "+" && operator2 == "-"){
            return (first + second) - third
        }
        else if(operator1 == "+" && operator2 == "x"){
            return first + (second * third)
        }
        else if(operator1 == "+" && operator2 == "÷"){
            return first + (second / third)
        }

        //Subtraction first
        if(operator1 == "-" && operator2 == "+"){
            return (first - second) + third
        }
        else if(operator1 == "-" && operator2 == "-"){
            return first - second - third
        }
        else if(operator1 == "-" && operator2 == "x"){
            return first - (second * third)
        }
        else if(operator1 == "-" && operator2 == "÷"){
            return first - (second / third)
        }

        //Multiplication first
        if(operator1 == "x" && operator2 == "+"){
            return (first * second) + third
        }
        else if(operator1 == "x" && operator2 == "-"){
            return (first * second) - third
        }
        else if(operator1 == "x" && operator2 == "x"){
            return (first * second) * third
        }
        else if(operator1 == "x" && operator2 == "÷"){
            return (first * second) / third
        }

        //Division first
        if(operator1 == "÷" && operator2 == "+"){
            return (first / second) + third
        }
        else if(operator1 == "÷" && operator2 == "-"){
            return (first / second) - third
        }
        else if(operator1 == "÷" && operator2 == "÷"){
            return (first / second) / third
        }
        else if(operator1 == "÷" && operator2 == "x"){
            return (first / second) * third
        }

        return 0
    }

    companion object{
        private const val TAG = "FireStore"
    }

}