package pt.isec.a21280210_a21280183_a21280330.tp_amov.game.multiplayer.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pt.isec.a21280210_a21280183_a21280330.tp_amov.databinding.ActivityMultiPlayerNetworkBinding

class MultiPlayerNetworkActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMultiPlayerNetworkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultiPlayerNetworkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Choose Server or Client Mode"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val colorDrawable = ColorDrawable(Color.parseColor("#FF018786"))
        supportActionBar?.setBackgroundDrawable(colorDrawable)

        binding.btnServer.setOnClickListener {
            startActivity(MultiPlayerGameActivity.getServerModeIntent(this))
        }

        binding.btnClient.setOnClickListener {
            startActivity(MultiPlayerGameActivity.getClientModeIntent(this))
        }
    }
}