package pt.isec.a21280210_a21280183_a21280330.tp_amov.game

import androidx.lifecycle.MutableLiveData
import pt.isec.a21280210_a21280183_a21280330.tp_amov.data.GameState
import pt.isec.a21280210_a21280183_a21280330.tp_amov.data.Level
import pt.isec.a21280210_a21280183_a21280330.tp_amov.data.Player

object GameModel {

    //Singleplayer
    private val operators : ArrayList<String> = ArrayList()
    var board : ArrayList<String> = ArrayList()
    var player : Player? = null
    lateinit var level : Level
    var gameState = MutableLiveData(GameState.AWAIT_BEGINNING)


    //Multiplayer
    const val SERVER_PORT = 9999
    var players : ArrayList<Player> = ArrayList()

    fun initializeLevel(){
        level = Level(1,3,3,2,60000,60000)
    }

    fun setupLevel(){

        when (level.number) {
            1 -> {
                level = Level(2,5,5,3,55000,55000)
            }
            2 -> {
                level = Level(3,7,7,4,50000,50000)
            }
            3 -> {
                level = Level(4,7,7,4,40000,40000)
            }
        }
    }

    fun setupBoard(){

        board = ArrayList(25)
        operators.addAll(mutableListOf("+","-","x","÷"))
        board.clear()

        if(level.number == 1){

            val randOperator = (0..0)
            val rand = (1..9)
            board.addAll(mutableListOf(rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],""
                ,operators[randOperator.random()],"",operators[randOperator.random()],rand.random().toString()
                ,operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],"",operators[randOperator.random()],"",operators[randOperator.random()],rand.random().toString()
                ,operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString()))
        }
        if(level.number == 2){

            val randOperator = (0..1)
            val rand = (1..10)
            board.addAll(mutableListOf(rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],""
                ,operators[randOperator.random()],"",operators[randOperator.random()],rand.random().toString()
                ,operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],"",operators[randOperator.random()],"",operators[randOperator.random()],rand.random().toString()
                ,operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString()))
        }
        if(level.number == 3){

            val randOperator = (0..2)
            val rand = (1..50)
            board.addAll(mutableListOf(rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],""
                ,operators[randOperator.random()],"",operators[randOperator.random()],rand.random().toString()
                ,operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],"",operators[randOperator.random()],"",operators[randOperator.random()],rand.random().toString()
                ,operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString()))
        }
        if(level.number == 4){

            val randOperator = (0..3)
            val rand = (1..99)
            board.addAll(mutableListOf(rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],""
                ,operators[randOperator.random()],"",operators[randOperator.random()],rand.random().toString()
                ,operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],"",operators[randOperator.random()],"",operators[randOperator.random()],rand.random().toString()
                ,operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString()))
        }
    }

    fun setupDinamicBoard(): ArrayList<String> {

        val tempBoard = ArrayList<String>()

        if(level.number == 1){

            val randOperator = (0..0)
            val rand = (1..9)
            tempBoard.addAll(mutableListOf(rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],""
                ,operators[randOperator.random()],"",operators[randOperator.random()],rand.random().toString()
                ,operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],"",operators[randOperator.random()],"",operators[randOperator.random()],rand.random().toString()
                ,operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString()))
        }
        if(level.number == 2){

            val randOperator = (0..1)
            val rand = (1..10)
            tempBoard.addAll(mutableListOf(rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],""
                ,operators[randOperator.random()],"",operators[randOperator.random()],rand.random().toString()
                ,operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],"",operators[randOperator.random()],"",operators[randOperator.random()],rand.random().toString()
                ,operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString()))
        }
        if(level.number == 3){

            val randOperator = (0..2)
            val rand = (1..50)
            tempBoard.addAll(mutableListOf(rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],""
                ,operators[randOperator.random()],"",operators[randOperator.random()],rand.random().toString()
                ,operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],"",operators[randOperator.random()],"",operators[randOperator.random()],rand.random().toString()
                ,operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString()))
        }
        if(level.number == 4){

            val randOperator = (0..3)
            val rand = (1..99)
            tempBoard.addAll(mutableListOf(rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],""
                ,operators[randOperator.random()],"",operators[randOperator.random()],rand.random().toString()
                ,operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],"",operators[randOperator.random()],"",operators[randOperator.random()],rand.random().toString()
                ,operators[randOperator.random()],rand.random().toString(),operators[randOperator.random()],rand.random().toString()))
        }

        return tempBoard

    }

    fun reset(){
        board.clear()
        level = Level(1,3,3,2,60000,60000)
        player = null
        gameState = MutableLiveData(GameState.AWAIT_BEGINNING)
        players.clear()
    }

}