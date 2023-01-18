package pt.isec.a21280210_a21280183_a21280330.tp_amov.game.singleplayer.activites

import android.graphics.BitmapFactory
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
import pt.isec.a21280210_a21280183_a21280330.tp_amov.database.LocalReader
import pt.isec.a21280210_a21280183_a21280330.tp_amov.databinding.ActivitySinglePlayerScoresBinding

class SinglePlayerScoresActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySinglePlayerScoresBinding
    private lateinit var db : FirebaseFirestore
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySinglePlayerScoresBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Scores"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val colorDrawable = ColorDrawable(Color.parseColor("#FF018786"))
        supportActionBar?.setBackgroundDrawable(colorDrawable)

        auth = Firebase.auth
        db = Firebase.firestore
        val collection = db.collection("Scores")


        val topScoresList = ArrayList<Int>()
        val topTimesList = ArrayList<String>()

        collection
            .orderBy("score", Query.Direction.DESCENDING)
            .whereEqualTo("playerId",auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener {
                for (i in 0 until it.size()){
                    topScoresList.add(it.documents[i].getLong("score")!!.toInt())
                    it.documents[i].getString("time")?.let { it1 -> topTimesList.add(it1) }

                }

                when(topScoresList.size){
                    0 -> return@addOnSuccessListener
                    1 -> {
                        val time : String = topTimesList[0]
                        val score : String = topScoresList[0].toString()
                        val info = "    Score: $score Time: $time min"
                        binding.infoGame1.text = info
                    }
                    2 -> {
                        var time : String = topTimesList[0]
                        var score : String = topScoresList[0].toString()
                        var info = "    Score: $score Time: $time min"
                        binding.infoGame1.text = info

                        time = topTimesList[1]
                        score = topScoresList[1].toString()
                        info = "    Score: $score Time: $time min"
                        binding.infoGame2.text = info
                    }
                    3 -> {
                        var time : String = topTimesList[0]
                        var score : String = topScoresList[0].toString()
                        var info = "    Score: $score Time: $time min"
                        binding.infoGame1.text = info

                        time = topTimesList[1]
                        score = topScoresList[1].toString()
                        info = "    Score: $score Time: $time min"
                        binding.infoGame2.text = info

                        time = topTimesList[2]
                        score = topScoresList[2].toString()
                        info = "    Score: $score Time: $time min"
                        binding.infoGame3.text = info

                    }
                    4 -> {
                        var time : String = topTimesList[0]
                        var score : String = topScoresList[0].toString()
                        var info = "    Score: $score Time: $time min"
                        binding.infoGame1.text = info

                        time = topTimesList[1]
                        score = topScoresList[1].toString()
                        info = "    Score: $score Time: $time min"
                        binding.infoGame2.text = info

                        time = topTimesList[2]
                        score = topScoresList[2].toString()
                        info = "    Score: $score Time: $time min"
                        binding.infoGame3.text = info

                        time = topTimesList[3]
                        score = topScoresList[3].toString()
                        info = "    Score: $score Time: $time min"
                        binding.infoGame4.text = info

                    }
                    else ->{
                        var time : String = topTimesList[0]
                        var score : String = topScoresList[0].toString()
                        var info = "    Score: $score Time: $time"
                        binding.infoGame1.text = info

                        time = topTimesList[1]
                        score = topScoresList[1].toString()
                        info = "    Score: $score Time: $time min"
                        binding.infoGame2.text = info

                        time = topTimesList[2]
                        score = topScoresList[2].toString()
                        info = "    Score: $score Time: $time min"
                        binding.infoGame3.text = info

                        time = topTimesList[3]
                        score = topScoresList[3].toString()
                        info = "    Score: $score Time: $time min"
                        binding.infoGame4.text = info

                        time = topTimesList[4]
                        score = topScoresList[4].toString()
                        info = "    Score: $score Time: $time min"
                        binding.infoGame5.text = info

                    }
                }

            }
        if(LocalReader.hasAvatar()) {
            val bitmap = BitmapFactory.decodeFile(LocalReader.getAvatarPath())
            binding.image1.setImageBitmap(bitmap)
            binding.image2.setImageBitmap(bitmap)
            binding.image3.setImageBitmap(bitmap)
            binding.image4.setImageBitmap(bitmap)
            binding.image5.setImageBitmap(bitmap)
        }
    }
}