package pt.isec.a21280210_a21280183_a21280330.tp_amov.game.multiplayer.activities

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pt.isec.a21280210_a21280183_a21280330.tp_amov.databinding.ActivityMultiPlayerMenuBinding

class MultiPlayerMenuActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMultiPlayerMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultiPlayerMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Multiplayer Mode"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val colorDrawable = ColorDrawable(Color.parseColor("#FF018786"))
        supportActionBar?.setBackgroundDrawable(colorDrawable)

        binding.btnStart.setOnClickListener{
            val intent = Intent(this, MultiPlayerNetworkActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        binding.btnScores5.setOnClickListener {
            val intent = Intent(this, MultiPlayerScoresActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        binding.btnTimer5.setOnClickListener {
            val intent = Intent(this, MultiPlayerTimesActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }
}