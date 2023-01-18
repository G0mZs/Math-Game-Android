package pt.isec.a21280210_a21280183_a21280330.tp_amov.game.multiplayer.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.isec.a21280210_a21280183_a21280330.tp_amov.databinding.ActivityMultiPlayerScoresBinding

class MultiPlayerScoresActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMultiPlayerScoresBinding
    private lateinit var db : FirebaseFirestore
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultiPlayerScoresBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Scores"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val colorDrawable = ColorDrawable(Color.parseColor("#FF018786"))
        supportActionBar?.setBackgroundDrawable(colorDrawable)

        auth = Firebase.auth
        db = Firebase.firestore
        val collection = db.collection("MultiPlayerScores")


        val topScoresList = ArrayList<Int>()
        val idList = ArrayList<String>()

        collection
            .orderBy("totalScore", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                for (i in 0 until it.size()) {
                    topScoresList.add(it.documents[i].getLong("totalScore")!!.toInt())
                    idList.add(it.documents[i].getString("gameId")!!)
                }

                when (topScoresList.size) {
                    0 -> return@addOnSuccessListener
                    1 -> {
                        val score: String = topScoresList[0].toString()
                        val info = "    Total Score: $score"
                        binding.infoGame1.text = info

                    }
                    2 -> {
                        var score: String = topScoresList[0].toString()
                        var info = "    Score: $score"
                        binding.infoGame1.text = info

                        score = topScoresList[1].toString()
                        info = "    Total Score: $score"
                        binding.infoGame2.text = info


                    }
                    3 -> {
                        var score: String = topScoresList[0].toString()
                        var info = "    Score: $score"
                        binding.infoGame1.text = info

                        score = topScoresList[1].toString()
                        info = "    Score: $score"
                        binding.infoGame2.text = info

                        score = topScoresList[2].toString()
                        info = "    Score: $score"
                        binding.infoGame3.text = info


                    }
                    4 -> {
                        var score: String = topScoresList[0].toString()
                        var info = "    Score: $score"
                        binding.infoGame1.text = info

                        score = topScoresList[1].toString()
                        info = "    Score: $score"
                        binding.infoGame2.text = info

                        score = topScoresList[2].toString()
                        info = "    Score: $score"
                        binding.infoGame3.text = info

                        score = topScoresList[3].toString()
                        info = "    Score: $score"
                        binding.infoGame4.text = info


                    }
                    else -> {
                        var score: String = topScoresList[0].toString()
                        var info = "    Score: $score"
                        binding.infoGame1.text = info

                        score = topScoresList[1].toString()
                        info = "    Score: $score"
                        binding.infoGame2.text = info

                        score = topScoresList[2].toString()
                        info = "    Score: $score"
                        binding.infoGame3.text = info

                        score = topScoresList[3].toString()
                        info = "    Score: $score"
                        binding.infoGame4.text = info

                        score = topScoresList[4].toString()
                        info = "    Score: $score"
                        binding.infoGame5.text = info

                    }
                }
            }



    }


}

