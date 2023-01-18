package pt.isec.a21280210_a21280183_a21280330.tp_amov

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pt.isec.a21280210_a21280183_a21280330.tp_amov.databinding.ActivityGameMenuBinding
import pt.isec.a21280210_a21280183_a21280330.tp_amov.game.multiplayer.activities.MultiPlayerMenuActivity
import pt.isec.a21280210_a21280183_a21280330.tp_amov.game.singleplayer.activites.SinglePlayerMenuActivity


class GameMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityGameMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Lets Play !"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val colorDrawable = ColorDrawable(Color.parseColor("#FF018786"))
        supportActionBar?.setBackgroundDrawable(colorDrawable)

        binding.btnSinglePlayer.setOnClickListener{
            val intent = Intent(this, SinglePlayerMenuActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        binding.btnMultiplayer.setOnClickListener {
            val intent = Intent(this, MultiPlayerMenuActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        binding.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }
}