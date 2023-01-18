package pt.isec.a21280210_a21280183_a21280330.tp_amov

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pt.isec.a21280210_a21280183_a21280330.tp_amov.database.LocalReader
import pt.isec.a21280210_a21280183_a21280330.tp_amov.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        LocalReader.filesDir = filesDir
        LocalReader.createIfUnavailable()

        supportActionBar?.hide()

        binding.btnPlay.setOnClickListener{
            val intent = Intent(this,GameMenuActivity::class.java)
            startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        binding.btnInfo.setOnClickListener{
            val intent = Intent(this,AboutUsActivity::class.java)
            startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

    }

    override fun onResume() {
        super.onResume()
        LocalReader.filesDir = filesDir
        LocalReader.createIfUnavailable()
    }

    override fun onRestart() {
        super.onRestart()
        LocalReader.filesDir = filesDir
        LocalReader.createIfUnavailable()
    }
}