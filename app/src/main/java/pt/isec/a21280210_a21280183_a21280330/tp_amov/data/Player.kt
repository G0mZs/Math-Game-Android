package pt.isec.a21280210_a21280183_a21280330.tp_amov.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.Socket

class Player : java.io.Serializable{

    var id: String? = null
    var name: String? = null
    var avatar : Bitmap? = null
    var socket : Socket? = null
    var score : Int = 0
    var correctAnswers : Int = 0
    var isWinner : Boolean = false
    var levelBoards : Int = 0
    var levelCompleted : Boolean = false
    var onGame : Boolean = true

    constructor(socket: Socket) {
        this.socket = socket

        var jsonObject = JSONObject()

        socket.getInputStream()?.run {
            try {
                jsonObject = JSONObject(this.bufferedReader().readLine())
            } catch (_: Exception) {
            }
        }

        id = jsonObject.getString(JSONHelper.JSONFields.PLAYER_ID)
        name = jsonObject.getString(JSONHelper.JSONFields.PLAYER_NAME_NAME)

        val decodedAvatarString = Base64.decode(
            jsonObject.getString(JSONHelper.JSONFields.AVATAR_NAME),
            Base64.URL_SAFE
        )

        avatar = BitmapFactory.decodeByteArray(
            decodedAvatarString,
            0,
            decodedAvatarString.size
        )
    }

    constructor(id : String, name : String){
        this.id = id
        this.name = name
    }

    constructor(id : String ,name: String, score : Int, correctAnswers : Int) {
        this.id = id
        this.name = name
        this.score = score
        this.correctAnswers = correctAnswers
    }

    fun getAsJsonObject() : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put(JSONHelper.JSONFields.PLAYER_ID, id)
        jsonObject.put(JSONHelper.JSONFields.PLAYER_NAME_NAME, name)
        jsonObject.put(JSONHelper.JSONFields.AVATAR_NAME, getBitmapAsEncodedString())
        jsonObject.put(JSONHelper.JSONFields.SCORE_NAME, score)
        jsonObject.put(JSONHelper.JSONFields.WINNER, isWinner)
        jsonObject.put(JSONHelper.JSONFields.LEVEL_COMPLETED, levelCompleted)
        jsonObject.put(JSONHelper.JSONFields.GAME_STATUS, onGame)

        return jsonObject
    }

    private fun getBitmapAsEncodedString() : String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        avatar?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

        val byteArray = byteArrayOutputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.URL_SAFE)
    }

}