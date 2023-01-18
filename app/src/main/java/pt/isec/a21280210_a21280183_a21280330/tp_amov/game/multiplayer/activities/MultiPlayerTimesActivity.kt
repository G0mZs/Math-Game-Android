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
import pt.isec.a21280210_a21280183_a21280330.tp_amov.databinding.ActivityMultiPlayerTimesBinding

class MultiPlayerTimesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMultiPlayerTimesBinding
    private lateinit var db : FirebaseFirestore
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultiPlayerTimesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Times"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val colorDrawable = ColorDrawable(Color.parseColor("#FF018786"))
        supportActionBar?.setBackgroundDrawable(colorDrawable)

        auth = Firebase.auth
        db = Firebase.firestore
        val collection = db.collection("MultiplayerTimes")

        val topTimesList = ArrayList<Int>()
        val idList = ArrayList<String>()

        collection
            .orderBy("totalTime", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                for (i in 0 until it.size()) {
                    topTimesList.add(it.documents[i].getLong("totalTime")!!.toInt())
                    idList.add(it.documents[i].getString("gameId")!!)
                }

                when (topTimesList.size) {
                    0 -> return@addOnSuccessListener
                    1 -> {
                        val minutes = (topTimesList[0].toLong() / 1000 / 60)
                        val seconds = (topTimesList[0].toLong() / 1000 % 60)

                        val duration = String.format("%02d,%02d",
                            minutes,seconds
                        )
                        val info = "    Total Time: $duration min"
                        binding.infoGame1.text = info

                    }
                    2 -> {
                        var minutes = (topTimesList[0].toLong() / 1000 / 60)
                        var seconds = (topTimesList[0].toLong() / 1000 % 60)

                        var duration = String.format("%02d,%02d",
                            minutes,seconds
                        )
                        var info = "    Total Time: $duration min"
                        binding.infoGame1.text = info

                        minutes = (topTimesList[1].toLong() / 1000 / 60)
                        seconds = (topTimesList[1].toLong() / 1000 % 60)

                        duration = String.format("%02d,%02d",
                            minutes,seconds
                        )

                        info = "    Total Time: $duration min"
                        binding.infoGame2.text = info

                    }
                    3 -> {
                        var minutes = (topTimesList[0].toLong() / 1000 / 60)
                        var seconds = (topTimesList[0].toLong() / 1000 % 60)

                        var duration = String.format("%02d,%02d",
                            minutes,seconds
                        )
                        var info = "    Total Time: $duration min"
                        binding.infoGame1.text = info

                        minutes = (topTimesList[1].toLong() / 1000 / 60)
                        seconds = (topTimesList[1].toLong() / 1000 % 60)

                        duration = String.format("%02d,%02d",
                            minutes,seconds
                        )

                        info = "    Total Time: $duration min"
                        binding.infoGame2.text = info

                        minutes = (topTimesList[2].toLong() / 1000 / 60)
                        seconds = (topTimesList[2].toLong() / 1000 % 60)

                        duration = String.format("%02d,%02d",
                            minutes,seconds
                        )

                        info = "    Total Time: $duration min"
                        binding.infoGame3.text = info


                    }
                    4 -> {
                        var minutes = (topTimesList[0].toLong() / 1000 / 60)
                        var seconds = (topTimesList[0].toLong() / 1000 % 60)

                        var duration = String.format("%02d,%02d",
                            minutes,seconds
                        )
                        var info = "    Total Time: $duration min"
                        binding.infoGame1.text = info

                        minutes = (topTimesList[1].toLong() / 1000 / 60)
                        seconds = (topTimesList[1].toLong() / 1000 % 60)

                        duration = String.format("%02d,%02d",
                            minutes,seconds
                        )

                        info = "    Total Time: $duration min"
                        binding.infoGame2.text = info

                        minutes = (topTimesList[2].toLong() / 1000 / 60)
                        seconds = (topTimesList[2].toLong() / 1000 % 60)

                        duration = String.format("%02d,%02d",
                            minutes,seconds
                        )

                        info = "    Total Time: $duration min"
                        binding.infoGame3.text = info

                        minutes = (topTimesList[3].toLong() / 1000 / 60)
                        seconds = (topTimesList[3].toLong() / 1000 % 60)

                        duration = String.format("%02d,%02d",
                            minutes,seconds
                        )

                        info = "    Total Time: $duration min"
                        binding.infoGame4.text = info



                    }
                    else -> {
                        var minutes = (topTimesList[0].toLong() / 1000 / 60)
                        var seconds = (topTimesList[0].toLong() / 1000 % 60)

                        var duration = String.format("%02d,%02d",
                            minutes,seconds
                        )
                        var info = "    Total Time: $duration min"
                        binding.infoGame1.text = info

                        minutes = (topTimesList[1].toLong() / 1000 / 60)
                        seconds = (topTimesList[1].toLong() / 1000 % 60)

                        duration = String.format("%02d,%02d",
                            minutes,seconds
                        )

                        info = "    Total Time: $duration min"
                        binding.infoGame2.text = info

                        minutes = (topTimesList[2].toLong() / 1000 / 60)
                        seconds = (topTimesList[2].toLong() / 1000 % 60)

                        duration = String.format("%02d,%02d",
                            minutes,seconds
                        )

                        info = "    Total Time: $duration min"
                        binding.infoGame3.text = info

                        minutes = (topTimesList[3].toLong() / 1000 / 60)
                        seconds = (topTimesList[3].toLong() / 1000 % 60)

                        duration = String.format("%02d,%02d",
                            minutes,seconds
                        )

                        info = "    Total Time: $duration min"
                        binding.infoGame4.text = info

                        minutes = (topTimesList[4].toLong() / 1000 / 60)
                        seconds = (topTimesList[4].toLong() / 1000 % 60)

                        duration = String.format("%02d,%02d",
                            minutes,seconds
                        )

                        info = "    Total Time: $duration min"
                        binding.infoGame5.text = info


                    }
                }
            }


    }

}
