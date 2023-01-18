package pt.isec.a21280210_a21280183_a21280330.tp_amov.game.singleplayer.activites

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.isec.a21280210_a21280183_a21280330.tp_amov.databinding.ActivitySinglePlayerMenuBinding

class SinglePlayerMenuActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySinglePlayerMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySinglePlayerMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Singleplayer Mode"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val colorDrawable = ColorDrawable(Color.parseColor("#FF018786"))
        supportActionBar?.setBackgroundDrawable(colorDrawable)

        binding.btnStart.setOnClickListener{
            val intent = Intent(this, SinglePlayerGameActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        binding.btnTop5.setOnClickListener {
            val intent = Intent(this, SinglePlayerScoresActivity::class.java)
            startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

    }
}