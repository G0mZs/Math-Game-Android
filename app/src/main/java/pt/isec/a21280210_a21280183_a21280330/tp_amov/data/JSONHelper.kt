package pt.isec.a21280210_a21280183_a21280330.tp_amov.data

object JSONHelper {

    // State of Game
    const val GAME_STATE_PLAYING = "gameStatePlaying"
    const val GAME_STATE_FINISHED = "gameStateFinished"
    const val GAME_STATE_CORRECT = "gameStateCorrect"
    const val GAME_STATE_SECOND_CORRECT = "gameStateSecond"
    const val GAME_STATE_WRONG = "gameStateWrong"
    const val GAME_STATE_PAUSED = "gameStatePaused"
    const val GAME_STATE_LOST = "gameStateLost"

    object JSONFields {
        const val GAME_STATE_NAME = "gameState" // String

        //Player Info
        const val PLAYERS_ARRAY_NAME = "playersArray"
        const val PLAYER_ID = "playerId" //String
        const val PLAYER_NAME_NAME = "playerName" // String
        const val AVATAR_NAME = "avatar" // Bitmap Encoded to String
        const val SCORE_NAME = "score" // Int
        const val WINNER = "winner" // Boolean
        const val LEVEL_COMPLETED = "levelCompleted" // Boolean
        const val GAME_STATUS = "gameStatus" // Boolean

        //Plays and Boards
        const val POSITION = "position" // Int
        const val POSITION_TYPE = "position_type" // String
        const val BOARD_NAME = "board" // Array of Int

    }
}