package pt.isec.a21280210_a21280183_a21280330.tp_amov.game.multiplayer

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import org.json.JSONObject
import pt.isec.a21280210_a21280183_a21280330.tp_amov.data.ConnectionState
import pt.isec.a21280210_a21280183_a21280330.tp_amov.data.GameState
import pt.isec.a21280210_a21280183_a21280330.tp_amov.data.JSONHelper
import pt.isec.a21280210_a21280183_a21280330.tp_amov.data.Player
import pt.isec.a21280210_a21280183_a21280330.tp_amov.database.LocalReader
import pt.isec.a21280210_a21280183_a21280330.tp_amov.game.GameModel
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.PrintStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.concurrent.thread

class MultiPlayerGameViewModel : ViewModel() {

    private var gameId : String? = null
    val gameModel = GameModel
    private var isGameStarted = false
    private var socket: Socket? = null
    private var serverSocket: ServerSocket? = null
    private var threadComm: Thread? = null
    var connectionState = MutableLiveData(ConnectionState.SETTING_PARAMETERS)
    private var firebase : FirebaseAuth = Firebase.auth
    private var db : FirebaseFirestore = Firebase.firestore
    private var startTime : Calendar? = null
    private var endTime : Calendar? = null
    private var totalTime : Long? = null

    fun startGame(){

        isGameStarted = true
        serverSocket?.close()

        startTime = Calendar.getInstance()

        gameModel.initializeLevel()
        gameModel.setupBoard()

        val jsonObject = JSONObject()

        val jsonPlayerArray = JSONArray()

        for (player: Player in GameModel.players) {
            jsonPlayerArray.put(player.getAsJsonObject())
        }

        jsonObject.put(JSONHelper.JSONFields.PLAYERS_ARRAY_NAME, jsonPlayerArray)
        jsonObject.put(JSONHelper.JSONFields.BOARD_NAME, JSONArray(gameModel.board))
        jsonObject.put(JSONHelper.JSONFields.GAME_STATE_NAME,JSONHelper.GAME_STATE_PLAYING)

        for (player: Player in gameModel.players) {
            if (player.socket == null) continue

            player.socket?.getOutputStream()?.run {
                thread {
                    try {
                        val printStream = PrintStream(this)
                        printStream.println(jsonObject)
                        printStream.flush()
                    } catch (_: Exception) {
                        stopGame()
                    }
                }
            }
        }

        gameModel.gameState.postValue(GameState.PLAYING)

    }

    fun startServer() {

        val player = Player(firebase.currentUser?.uid.toString(),LocalReader.getName(),0,0)
        player.avatar = BitmapFactory.decodeFile(LocalReader.getAvatarPath())
        gameModel.player = player
        gameModel.players.add(player)

        if (serverSocket != null || socket != null || connectionState.value != ConnectionState.SETTING_PARAMETERS){
            return
        }

        connectionState.postValue(ConnectionState.SERVER_CONNECTING)

        thread {
            serverSocket = ServerSocket(gameModel.SERVER_PORT)

                while(!isGameStarted) {

                    try {
                        val socketClient = serverSocket!!.accept()
                        startCommServer(socketClient)

                    } catch (_: Exception) {
                        connectionState.postValue(ConnectionState.CONNECTION_ERROR)
                    } finally {

                    }
                }
        }
    }

    fun stopServer() {
        serverSocket?.close()
        connectionState.postValue(ConnectionState.CONNECTION_ENDED)
        serverSocket = null
    }

    fun startClient(serverIP: String,serverPort: Int = gameModel.SERVER_PORT) {
        if (socket != null || connectionState.value != ConnectionState.SETTING_PARAMETERS){
            return
        }

        thread {
            connectionState.postValue(ConnectionState.CLIENT_CONNECTING)
            try {
                val newsocket = Socket()
                newsocket.connect(InetSocketAddress(serverIP,serverPort),5000)

                gameModel.player = Player(firebase.currentUser?.uid.toString(),LocalReader.getName(),0,0)
                gameModel.player!!.avatar = BitmapFactory.decodeFile(LocalReader.getAvatarPath())
                startCommClient(newsocket)
            } catch (_: Exception) {
                //connectionState.postValue(ConnectionState.CONNECTION_ERROR)
                //stopGame()
            }
        }
    }

    private fun startCommServer(newSocket: Socket) {


        val inputStream : InputStream = newSocket.getInputStream()

        thread {
            try {
                val newPlayer = Player(newSocket)
                gameModel.players.add(newPlayer)
                Log.i("shshshss","sjsd")

                connectionState.postValue(ConnectionState.CONNECTION_ESTABLISHED)
                val bufI = inputStream.bufferedReader()

                while (gameModel.gameState.value != GameState.FINISHED) {

                    val jsonObject = JSONObject(bufI.readLine())

                    val jsonPosition = jsonObject.getInt(JSONHelper.JSONFields.POSITION)
                    val jsonPositionType = jsonObject.getString(JSONHelper.JSONFields.POSITION_TYPE)

                    val gameBoard = ArrayList<String>()
                    val jsonBoard = jsonObject.getJSONArray(JSONHelper.JSONFields.BOARD_NAME)
                    for (i in 0 until jsonBoard.length()) {
                        gameBoard.add(jsonBoard.getString(i))
                    }

                    checkPlay(jsonPosition, jsonPositionType, CLIENT_MODE, gameBoard, newSocket)
                    nextLevel()
                }
            } catch (_: Exception) {
            } finally {
                //stopGame()
            }
        }
    }

    private fun startCommClient(newSocket: Socket) {

        socket = newSocket
        val inputStream : InputStream = newSocket.getInputStream()

        thread {
            try {
                connectionState.postValue(ConnectionState.CONNECTION_ESTABLISHED)
                sendClientData()

                val bufI = inputStream.bufferedReader()

                while (gameModel.gameState.value != GameState.FINISHED) {

                    val jsonObject = JSONObject(bufI.readLine())

                    val jsonPlayerArray = jsonObject.getJSONArray(JSONHelper.JSONFields.PLAYERS_ARRAY_NAME)
                    val players = ArrayList<Player>()

                    for (i in 0 until jsonPlayerArray.length()) {

                        val name = jsonPlayerArray.getJSONObject(i).getString(JSONHelper.JSONFields.PLAYER_NAME_NAME)
                        val id = jsonPlayerArray.getJSONObject(i).getString(JSONHelper.JSONFields.PLAYER_ID)

                        val encodedString = jsonPlayerArray.getJSONObject(i).getString(JSONHelper.JSONFields.AVATAR_NAME)
                        val decodedString = Base64.decode(encodedString, Base64.URL_SAFE)
                        val avatar = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

                        val player = Player(id,name)
                        player.avatar = avatar
                        player.score = jsonPlayerArray.getJSONObject(i).getInt(JSONHelper.JSONFields.SCORE_NAME)

                        player.isWinner = jsonPlayerArray.getJSONObject(i).getBoolean(JSONHelper.JSONFields.WINNER)
                        player.levelCompleted = jsonPlayerArray.getJSONObject(i).getBoolean(JSONHelper.JSONFields.LEVEL_COMPLETED)
                        player.onGame = jsonPlayerArray.getJSONObject(i).getBoolean(JSONHelper.JSONFields.GAME_STATUS)

                        players.add(player)
                    }

                    gameModel.players = players

                    val gameBoard = ArrayList<String>()
                    val jsonBoard = jsonObject.getJSONArray(JSONHelper.JSONFields.BOARD_NAME)
                    for (i in 0 until jsonBoard.length()) {
                        gameBoard.add(jsonBoard.getString(i))
                    }
                    gameModel.board = gameBoard

                    when (jsonObject.getString(JSONHelper.JSONFields.GAME_STATE_NAME)) {
                        JSONHelper.GAME_STATE_CORRECT -> {
                            gameModel.gameState.postValue(GameState.CORRECT_ANSWER)
                        }
                        JSONHelper.GAME_STATE_SECOND_CORRECT -> {
                            gameModel.gameState.postValue(GameState.SECOND_CORRECT_ANSWER)
                        }
                        JSONHelper.GAME_STATE_WRONG -> {
                            gameModel.gameState.postValue(GameState.WRONG_ANSWER)
                        }
                        JSONHelper.GAME_STATE_PAUSED -> {
                            gameModel.setupLevel()
                            gameModel.gameState.postValue(GameState.PAUSED)
                        }
                        JSONHelper.GAME_STATE_FINISHED -> {
                            gameModel.gameState.postValue(GameState.FINISHED)
                        }
                        JSONHelper.GAME_STATE_LOST -> {
                            gameModel.gameState.postValue(GameState.LOST)
                        }
                        else -> {
                            gameModel.gameState.postValue(GameState.PLAYING)
                        }
                    }

                }
            } catch (_: Exception) {
            } finally {
                //stopGame()
            }
        }
    }

    private fun stopGame() {
        try {
            //_state.postValue(State.GAME_OVER)
            connectionState.postValue(ConnectionState.CONNECTION_ERROR)
            socket?.close()
            socket = null
            threadComm?.interrupt()
            threadComm = null
        } catch (_: Exception) { }
    }

    fun makeMove(position: Int, type: String, intent: Intent, board: ArrayList<String>){


        when(intent.getIntExtra("mode", SERVER_MODE)){
            SERVER_MODE -> {
                checkPlay(position, type, SERVER_MODE, board, null)
                nextLevelServer()
                nextLevel()
            }
            CLIENT_MODE -> {
                sendMoveToServer(position,type,board)
            }
        }


    }

    private fun nextLevelServer(){


         if (gameModel.players[0].levelBoards == gameModel.level.nBoards) {
             gameModel.players[0].levelBoards = 0

             val isLevelWon = checkIfLevelIsPassed(gameModel.players[0])

             if (isLevelWon) {
                 gameModel.players[0].levelCompleted = true
                 val isFirst = checkIfFirstOnLevel(gameModel.players[0])

                 if (isFirst) {
                     gameModel.players[0].score += 5
                 }

                 gameModel.players[0].correctAnswers = 0
                 gameModel.gameState.postValue(GameState.PAUSED)

             } else {
                 gameModel.players[0].onGame = false
                 gameModel.gameState.postValue(GameState.LOST)
             }
         } else {
             gameModel.setupBoard()
         }

    }

    private fun sendMoveToServer(position: Int, type: String, board : ArrayList<String>){

        val jsonObject = JSONObject()
        jsonObject.put(JSONHelper.JSONFields.POSITION, position)
        jsonObject.put(JSONHelper.JSONFields.POSITION_TYPE, type)
        jsonObject.put(JSONHelper.JSONFields.BOARD_NAME, JSONArray(board))

            socket?.getOutputStream()?.run {
                thread {
                    try {
                        val printStream = PrintStream(this)
                        printStream.println(jsonObject)
                        printStream.flush()
                    } catch (_: Exception) {
                        stopGame()
                    }
                }
            }
    }

    private fun sendClientData(){

        val jsonObject = JSONObject()
        jsonObject.put(JSONHelper.JSONFields.PLAYER_ID, gameModel.player!!.id)
        jsonObject.put(JSONHelper.JSONFields.PLAYER_NAME_NAME, gameModel.player!!.name)

        val byteArrayOutputStream = ByteArrayOutputStream()
        gameModel.player!!.avatar?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

        val byteArray = byteArrayOutputStream.toByteArray()

        jsonObject.put(JSONHelper.JSONFields.AVATAR_NAME,Base64.encodeToString(byteArray, Base64.URL_SAFE))

        socket?.getOutputStream()?.run {
            thread {
                try {
                    val printStream = PrintStream(this)
                    printStream.println(jsonObject)
                    printStream.flush()
                } catch (_: Exception) {
                    stopGame()
                }
            }
        }
    }

    private fun updateClientData(socketCli: Socket?, gameState: GameState){

        val jsonObject = JSONObject()

        for (i in 0 until gameModel.players.size) {

            if (gameModel.players[i].socket == socketCli) {

                when (gameState) {
                    GameState.CORRECT_ANSWER -> {

                        gameModel.players[i].score += 2
                        gameModel.players[i].correctAnswers++
                        gameModel.players[i].levelBoards++
                    }
                    GameState.SECOND_CORRECT_ANSWER -> {

                        gameModel.players[i].score += 1
                        gameModel.players[i].correctAnswers++
                        gameModel.players[i].levelBoards++
                    }
                    else -> {
                        gameModel.players[i].levelBoards++
                    }
                }


                if (gameModel.players[i].levelBoards == gameModel.level.nBoards) {

                    gameModel.players[i].levelBoards = 0
                    val isLevelWon = checkIfLevelIsPassed(gameModel.players[i])

                    if (isLevelWon) {
                        gameModel.players[i].levelCompleted = true
                        val isFirst = checkIfFirstOnLevel(gameModel.players[i])

                        if (isFirst) {
                            gameModel.players[i].score += 5
                        }

                        gameModel.players[i].correctAnswers = 0
                        jsonObject.put(
                            JSONHelper.JSONFields.GAME_STATE_NAME,
                            JSONHelper.GAME_STATE_PAUSED
                        )

                        if(gameModel.level.number == 4){
                            gameModel.players[i].isWinner = true
                        }
                    } else {
                        gameModel.players[i].onGame = false
                        jsonObject.put(
                            JSONHelper.JSONFields.GAME_STATE_NAME,
                            JSONHelper.GAME_STATE_LOST
                        )
                    }
                } else {

                    jsonObject.put(
                        JSONHelper.JSONFields.GAME_STATE_NAME, when (gameState) {
                            GameState.CORRECT_ANSWER -> JSONHelper.GAME_STATE_CORRECT
                            GameState.SECOND_CORRECT_ANSWER -> JSONHelper.GAME_STATE_SECOND_CORRECT
                            else -> JSONHelper.GAME_STATE_WRONG
                        }
                    )
                }
            }

        }

        socketCli?.getOutputStream()?.run {
            thread {
                try {

                    val jsonPlayerArray = JSONArray()

                    for (player: Player in GameModel.players) {
                        jsonPlayerArray.put(player.getAsJsonObject())
                    }

                    jsonObject.put(JSONHelper.JSONFields.PLAYERS_ARRAY_NAME, jsonPlayerArray)
                    jsonObject.put(JSONHelper.JSONFields.BOARD_NAME, JSONArray(gameModel.setupDinamicBoard()))

                    val printStream = PrintStream(this)
                    printStream.println(jsonObject)
                    printStream.flush()
                } catch (_: Exception) {
                    stopGame()
                }
            }
        }



    }

   private fun checkIfFirstOnLevel(player: Player) : Boolean{

       var playersOnLevel = 0

       for(i in 0 until gameModel.players.size){
           if(player != gameModel.players[i] && gameModel.players[i].levelCompleted){
               playersOnLevel++
           }
       }

       if(playersOnLevel == 0){
           return true
       }

       return false
   }

    private fun checkIfLevelIsPassed(player: Player) : Boolean{

        return player.correctAnswers >= gameModel.level.minAnswers
    }

    private fun nextLevel() {

            var isLevelCompleted = 0

            for (player: Player in gameModel.players) {
                if (player.levelCompleted || !player.onGame) {
                    isLevelCompleted++
                }
            }

            if (isLevelCompleted == gameModel.players.size) {

                var lost = 0

                for(player : Player in gameModel.players){
                    if(!player.onGame)
                        lost++
                }


                if(gameModel.level.number == 4 || lost == gameModel.players.size){
                    gameModel.setupBoard()

                    val jsonObject = JSONObject()
                    val jsonPlayerArray = JSONArray()

                    for (player: Player in GameModel.players) {
                        jsonPlayerArray.put(player.getAsJsonObject())
                    }

                    jsonObject.put(JSONHelper.JSONFields.PLAYERS_ARRAY_NAME, jsonPlayerArray)
                    jsonObject.put(JSONHelper.JSONFields.BOARD_NAME, JSONArray(gameModel.board))
                    jsonObject.put(JSONHelper.JSONFields.GAME_STATE_NAME, JSONHelper.GAME_STATE_FINISHED)

                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                for (player: Player in gameModel.players) {
                                    if (player.socket == null) continue
                                player.socket?.getOutputStream()?.run {
                                    thread{
                                        try {
                                            val printStream = PrintStream(this)
                                            printStream.println(jsonObject)
                                            printStream.flush()
                                        } catch (_: Exception) {
                                            stopGame()
                                        }
                                    }
                                }
                            }
                            },
                            500 // value in milliseconds
                        )
                    if(gameModel.players[0].onGame){
                        gameModel.players[0].correctAnswers = 0
                        gameModel.players[0].isWinner = true
                    }
                    endTime = Calendar.getInstance()

                    val millis1 = startTime?.timeInMillis
                    val millis2 = endTime?.timeInMillis
                    totalTime = millis2!! - millis1!!

                    gameId = UUID.randomUUID().toString()
                    updateTopTimes()
                    updateTopScores()
                    gameModel.gameState.postValue(GameState.FINISHED)

                }else{

                    for(i in 0 until gameModel.players.size){
                        gameModel.players[i].levelCompleted = false
                    }

                    gameModel.setupLevel()
                    gameModel.setupBoard()


                    val jsonObject = JSONObject()
                    val jsonPlayerArray = JSONArray()

                    for (player: Player in GameModel.players) {
                        jsonPlayerArray.put(player.getAsJsonObject())
                    }

                    jsonObject.put(JSONHelper.JSONFields.PLAYERS_ARRAY_NAME, jsonPlayerArray)
                    jsonObject.put(JSONHelper.JSONFields.BOARD_NAME, JSONArray(gameModel.board))
                    jsonObject.put(JSONHelper.JSONFields.GAME_STATE_NAME, JSONHelper.GAME_STATE_PLAYING)

                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                    for (player: Player in gameModel.players) {
                        if (player.socket == null || !player.onGame) continue

                        player.socket?.getOutputStream()?.run {
                            thread{

                                try {
                                    val printStream = PrintStream(this)
                                    printStream.println(jsonObject)
                                    printStream.flush()
                                } catch (_: Exception) {
                                    stopGame()
                                }
                            }
                        }
                    }
                    if(gameModel.players[0].onGame){
                        gameModel.gameState.postValue(GameState.PLAYING)
                    }
                        },
                        500 // value in milliseconds
                    )
                }
            }
    }


    private fun checkPlay(
        position: Int,
        type: String,
        intent: Int,
        board: ArrayList<String>,
        socketCli: Socket?
    ){

        val row1 : Int = calculateRowOrColumn(
            board[0].toInt(),
            board[2].toInt(),
            board[4].toInt(),
            board[1],
            board[3])

        val row3 : Int = calculateRowOrColumn(
            board[10].toInt(),
            board[12].toInt(),
            board[14].toInt(),
            board[11],
            board[13])

        val row5 : Int = calculateRowOrColumn(
            board[20].toInt(),
            board[22].toInt(),
            board[24].toInt(),
            board[21],
            board[23])

        val column1 : Int = calculateRowOrColumn(
            board[0].toInt(),
            board[10].toInt(),
            board[20].toInt(),
            board[5],
            board[15])

        val column3 : Int = calculateRowOrColumn(
            board[2].toInt(),
            board[12].toInt(),
            board[22].toInt(),
            board[7],
            board[17])
        val column5 : Int = calculateRowOrColumn(
            board[4].toInt(),
            board[14].toInt(),
            board[24].toInt(),
            board[9],
            board[19])

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
            checkRow(position, first, second, row1, row3, row5, intent, socketCli)
        }
        else if(type == "column") {
            checkColumn(position, first, second, column1, column3, column5, intent, socketCli)
        }

    }

    private fun checkRow(
        position: Int,
        first: Int,
        second: Int,
        row1: Int,
        row3: Int,
        row5: Int,
        intent: Int,
        socketCli: Socket?
    ){

        when(position){
            1 -> {
                when(row1){
                    first -> {
                        when(intent){
                            SERVER_MODE -> {
                                gameModel.players[0].score += 2
                                gameModel.players[0].correctAnswers++
                                gameModel.players[0].levelBoards++
                                gameModel.gameState.postValue(GameState.CORRECT_ANSWER)
                            }
                            CLIENT_MODE -> {
                                updateClientData(socketCli,GameState.CORRECT_ANSWER)
                            }
                        }
                    }
                    second -> {
                        when(intent){
                            SERVER_MODE -> {
                                gameModel.players[0].score += 1
                                gameModel.players[0].correctAnswers++
                                gameModel.players[0].levelBoards++
                                gameModel.gameState.postValue(GameState.SECOND_CORRECT_ANSWER)
                            }
                            CLIENT_MODE -> {
                                updateClientData(socketCli,GameState.SECOND_CORRECT_ANSWER)
                            }
                        }
                    }
                    else ->
                    {
                        when(intent){
                            SERVER_MODE -> {
                                gameModel.players[0].levelBoards++
                                gameModel.gameState.postValue(GameState.WRONG_ANSWER)
                            }
                            CLIENT_MODE -> {
                                updateClientData(socketCli,GameState.WRONG_ANSWER)
                            }
                        }
                    }
                }
            }
            3 -> {
                when(row3) {
                    first -> {
                        when(intent){
                            SERVER_MODE -> {
                                gameModel.players[0].score += 2
                                gameModel.players[0].correctAnswers++
                                gameModel.players[0].levelBoards++
                                gameModel.gameState.postValue(GameState.CORRECT_ANSWER)
                            }
                            CLIENT_MODE -> {
                                updateClientData(socketCli,GameState.CORRECT_ANSWER)
                            }
                        }
                    }
                    second -> {
                        when(intent){
                            SERVER_MODE -> {
                                gameModel.players[0].score += 1
                                gameModel.players[0].correctAnswers++
                                gameModel.players[0].levelBoards++
                                gameModel.gameState.postValue(GameState.SECOND_CORRECT_ANSWER)
                            }
                            CLIENT_MODE -> {
                                updateClientData(socketCli,GameState.SECOND_CORRECT_ANSWER)
                            }
                        }
                    }
                    else -> {
                        when(intent){
                            SERVER_MODE -> {
                                gameModel.players[0].levelBoards++
                                gameModel.gameState.postValue(GameState.WRONG_ANSWER)
                            }
                            CLIENT_MODE -> {
                                updateClientData(socketCli,GameState.WRONG_ANSWER)
                            }
                        }
                    }
                }
            }
            5 -> {
                when(row5){
                    first -> {
                        when(intent){
                            SERVER_MODE -> {
                                gameModel.players[0].score += 2
                                gameModel.players[0].correctAnswers++
                                gameModel.players[0].levelBoards++
                                gameModel.gameState.postValue(GameState.CORRECT_ANSWER)
                            }
                            CLIENT_MODE -> {
                                updateClientData(socketCli,GameState.CORRECT_ANSWER)
                            }
                        }
                    }
                    second -> {
                        when(intent){
                            SERVER_MODE -> {
                                gameModel.players[0].score += 1
                                gameModel.players[0].correctAnswers++
                                gameModel.players[0].levelBoards++
                                gameModel.gameState.postValue(GameState.SECOND_CORRECT_ANSWER)
                            }
                            CLIENT_MODE -> {
                                updateClientData(socketCli,GameState.SECOND_CORRECT_ANSWER)
                            }
                        }
                    }
                    else -> {
                        when(intent){
                            SERVER_MODE -> {
                                gameModel.players[0].levelBoards++
                                gameModel.gameState.postValue(GameState.WRONG_ANSWER)
                            }
                            CLIENT_MODE -> {
                                updateClientData(socketCli,GameState.WRONG_ANSWER)
                            }
                        }
                    }
                }
            }

        }
    }

    private fun checkColumn(
        position: Int,
        first: Int,
        second: Int,
        column1: Int,
        column3: Int,
        column5: Int,
        intent: Int,
        socketCli: Socket?
    ){

        when(position){
            1 -> {
                when(column1){
                    first -> {
                        when(intent){
                            SERVER_MODE -> {
                                gameModel.players[0].score += 2
                                gameModel.players[0].correctAnswers++
                                gameModel.players[0].levelBoards++
                                gameModel.gameState.postValue(GameState.CORRECT_ANSWER)
                            }
                            CLIENT_MODE -> {
                                updateClientData(socketCli,GameState.CORRECT_ANSWER)
                            }
                        }
                    }
                    second -> {
                        when(intent){
                            SERVER_MODE -> {
                                gameModel.players[0].score += 1
                                gameModel.players[0].correctAnswers++
                                gameModel.players[0].levelBoards++
                                gameModel.gameState.postValue(GameState.SECOND_CORRECT_ANSWER)
                            }
                            CLIENT_MODE -> {
                                updateClientData(socketCli,GameState.SECOND_CORRECT_ANSWER)
                            }
                        }
                    }
                    else -> {
                        when(intent){
                            SERVER_MODE -> {
                                gameModel.players[0].levelBoards++
                                gameModel.gameState.postValue(GameState.WRONG_ANSWER)
                            }
                            CLIENT_MODE -> {
                                updateClientData(socketCli,GameState.WRONG_ANSWER)
                            }
                        }
                    }
                }
            }
            3 -> {
                when(column3) {
                    first -> {
                        when(intent){
                            SERVER_MODE -> {
                                gameModel.players[0].score += 2
                                gameModel.players[0].correctAnswers++
                                gameModel.players[0].levelBoards++
                                gameModel.gameState.postValue(GameState.CORRECT_ANSWER)
                            }
                            CLIENT_MODE -> {
                                updateClientData(socketCli,GameState.CORRECT_ANSWER)
                            }
                        }
                    }
                    second -> {
                        when(intent){
                            SERVER_MODE -> {
                                gameModel.players[0].score += 1
                                gameModel.players[0].correctAnswers++
                                gameModel.players[0].levelBoards++
                                gameModel.gameState.postValue(GameState.SECOND_CORRECT_ANSWER)
                            }
                            CLIENT_MODE -> {
                                updateClientData(socketCli,GameState.SECOND_CORRECT_ANSWER)
                            }
                        }
                    }
                    else -> {
                        when(intent){
                            SERVER_MODE -> {
                                gameModel.players[0].levelBoards++
                                gameModel.gameState.postValue(GameState.WRONG_ANSWER)
                            }
                            CLIENT_MODE -> {
                                updateClientData(socketCli,GameState.WRONG_ANSWER)
                            }
                        }
                    }
                }
            }
            5 -> {
                when(column5){
                    first -> {
                        when(intent){
                            SERVER_MODE -> {
                                gameModel.players[0].score += 2
                                gameModel.players[0].correctAnswers++
                                gameModel.players[0].levelBoards++
                                gameModel.gameState.postValue(GameState.CORRECT_ANSWER)
                            }
                            CLIENT_MODE -> {
                                updateClientData(socketCli,GameState.CORRECT_ANSWER)
                            }
                        }
                    }
                    second -> {
                        when(intent){
                            SERVER_MODE -> {
                                gameModel.players[0].score += 1
                                gameModel.players[0].correctAnswers++
                                gameModel.players[0].levelBoards++
                                gameModel.gameState.postValue(GameState.SECOND_CORRECT_ANSWER)
                            }
                            CLIENT_MODE -> {
                                updateClientData(socketCli,GameState.SECOND_CORRECT_ANSWER)
                            }
                        }
                    }
                    else -> {
                        when(intent){
                            SERVER_MODE -> {
                                gameModel.players[0].levelBoards++
                                gameModel.gameState.postValue(GameState.WRONG_ANSWER)
                            }
                            CLIENT_MODE -> {
                                updateClientData(socketCli,GameState.WRONG_ANSWER)
                            }
                        }
                    }
                }
            }
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

    private fun updateTopTimes(){
        val collection = db.collection("MultiplayerTimes")
        collection
            .orderBy("totalTime", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener {
                Log.i(TAG, "updateDataInFireStore: Success")
                if (it.size() < 5) {

                    collection.document()
                        .set(
                            hashMapOf(
                                "totalTime" to totalTime,
                                "gameId" to gameId
                            )
                        )

                        for(i in 0 until gameModel.players.size){
                            collection.document()
                                .set(
                                    hashMapOf(
                                        "gameId" to gameId,
                                        "player" to gameModel.players[i].name,
                                        "score" to gameModel.players[i].score,
                                        "onGame" to gameModel.players[i].onGame
                                    )
                                )
                        }
                }
                else{
                    if (it.documents[0].getLong("totalTime")!! >= totalTime!!)
                        return@addOnSuccessListener

                    it.documents[0].reference.delete()
                    collection
                        .whereEqualTo("gameId",it.documents[0].getString("gameId"))
                        .get()
                        .addOnSuccessListener{ db ->
                            for(i in 0 until db.size()){
                                db.documents[i].reference.delete()
                            }
                        }

                    collection.document()
                        .set(
                            hashMapOf(
                                "totalTime" to totalTime,
                                "gameId" to gameId
                            )
                        )

                    for(i in 0 until gameModel.players.size){
                        collection.document()
                            .set(
                                hashMapOf(
                                    "gameId" to gameId,
                                    "player" to gameModel.players[i].name,
                                    "score" to gameModel.players[i].score,
                                    "onGame" to gameModel.players[i].onGame
                                )
                            )
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.i(TAG, "updateDataInFireStore: ${e.message}")
            }
    }

    private fun updateTopScores() {

        var totalScore = 0

        for(i in 0 until gameModel.players.size){
            totalScore += gameModel.players[i].score
        }

        val collection = db.collection("MultiPlayerScores")
        collection
            .orderBy("totalScore", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener {
                Log.i(TAG, "updateDataInFireStore: Success")
                if (it.size() < 5) {

                    collection.document()
                        .set(
                            hashMapOf(
                                "totalScore" to totalScore,
                                "gameId" to gameId
                            )
                        )

                    for(i in 0 until gameModel.players.size){
                        collection.document()
                            .set(
                                hashMapOf(
                                    "gameId" to gameId,
                                    "player" to gameModel.players[i].name,
                                    "score" to gameModel.players[i].score,
                                    "onGame" to gameModel.players[i].onGame
                                )
                            )
                    }

                }
                else{
                    if (it.documents[0].getLong("totalScore")!! >= totalScore)
                        return@addOnSuccessListener

                    it.documents[0].reference.delete()
                    collection
                        .whereEqualTo("gameId",it.documents[0].getString("gameId"))
                        .get()
                        .addOnSuccessListener{ db ->
                            for(i in 0 until db.size()){
                                db.documents[i].reference.delete()
                            }
                        }

                    collection.document()
                        .set(
                            hashMapOf(
                                "totalScore" to totalScore,
                                "gameId" to gameId
                            )
                        )

                    for(i in 0 until gameModel.players.size){
                        collection.document()
                            .set(
                                hashMapOf(
                                    "gameId" to gameId,
                                    "player" to gameModel.players[i].name,
                                    "score" to gameModel.players[i].score,
                                    "onGame" to gameModel.players[i].onGame
                                )
                            )
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.i(TAG, "updateDataInFireStore: ${e.message}")
            }
    }

    fun getBoard(): ArrayList<String> {
        return gameModel.board
    }

    fun getScore(): Int{

        var score = 0

        for(player : Player in gameModel.players){
            if(player.id == gameModel.player!!.id){
                score = player.score
            }
        }

        return score
    }

    companion object{
        private const val TAG = "FireStore"
        private const val SERVER_MODE = 0
        private const val CLIENT_MODE = 1
    }
}