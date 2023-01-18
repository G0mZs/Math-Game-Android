package pt.isec.a21280210_a21280183_a21280330.tp_amov.game.singleplayer.activites

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import pt.isec.a21280210_a21280183_a21280330.tp_amov.data.GameState
import pt.isec.a21280210_a21280183_a21280330.tp_amov.databinding.ActivitySinglePlayerGameBinding
import pt.isec.a21280210_a21280183_a21280330.tp_amov.databinding.DialogBackBinding
import pt.isec.a21280210_a21280183_a21280330.tp_amov.databinding.DialogLoseBinding
import pt.isec.a21280210_a21280183_a21280330.tp_amov.databinding.DialogWinBinding
import pt.isec.a21280210_a21280183_a21280330.tp_amov.game.singleplayer.OnSwipeTouchSpListener
import pt.isec.a21280210_a21280183_a21280330.tp_amov.game.singleplayer.SinglePlayerGameViewModel
import pt.isec.a21280210_a21280183_a21280330.tp_amov.game.singleplayer.SinglePlayerGridAdapter


class SinglePlayerGameActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySinglePlayerGameBinding
    private lateinit var viewModel : SinglePlayerGameViewModel
    private lateinit var gridView: GridView
    private lateinit var txtPaused : TextView
    private lateinit var txtTimer : TextView
    private lateinit var timer: CountDownTimer
    private lateinit var pause : CountDownTimer
    private lateinit var myGridAdapter: SinglePlayerGridAdapter
    private var dlg: Dialog? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySinglePlayerGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gridView = binding.myGrid1
        txtPaused = binding.txtPaused1
        txtPaused.visibility = View.INVISIBLE
        txtTimer = binding.txtTimer1

        supportActionBar?.hide()

        viewModel = ViewModelProvider(this)[SinglePlayerGameViewModel::class.java]
        viewModel.setupGame()
        viewModel.gameModel.gameState.observe(this, observerGameState())

        val txtScore = "Score: " + viewModel.gameModel.player!!.score.toString()
        binding.txtScore1.text = txtScore

        val txtLevel = "LEVEL " + viewModel.gameModel.level.number.toString()
        binding.txtLevel1.text = txtLevel

        myGridAdapter = SinglePlayerGridAdapter(viewModel, this)
        gridView.adapter = myGridAdapter

        gridView.setOnTouchListener(object : OnSwipeTouchSpListener(this@SinglePlayerGameActivity,gridView,viewModel){
            override fun onSwipe() {
               myGridAdapter.notifyDataSetChanged()
            }
        })

    }

    override fun onDestroy() {
        timer.cancel()
        viewModel.gameModel.reset()
        super.onDestroy()
    }


    private fun observerGameState() = Observer<GameState> {
        when(it){
            GameState.PLAYING -> {
                txtPaused.visibility = View.INVISIBLE
                gridView.visibility = View.VISIBLE

                timer = object : CountDownTimer(viewModel.gameModel.level.time.toLong(), 1000) {

                    override fun onTick(remaining: Long) {
                        val seconds : Int = (remaining/1000).toInt()
                        val msg = "00:$seconds "
                        viewModel.gameModel.level.time -= 1000
                        viewModel.totalTime += 1000
                        txtTimer.text = msg
                    }

                    override fun onFinish() {
                        viewModel.checkCorrectAnswers()
                    }
                }.start()
            }
            GameState.PAUSED -> {

                txtPaused.visibility = View.VISIBLE
                val txtLevel = "LEVEL " + viewModel.gameModel.level.number.toString()
                val txtScore = "Score: " + viewModel.gameModel.player!!.score.toString()
                binding.txtScore1.text = txtScore
                binding.txtLevel1.text = txtLevel
                gridView.visibility = View.INVISIBLE
                timer.cancel()

                pause = object : CountDownTimer(5000, 1000) {

                    override fun onTick(remaining: Long) {
                        val seconds : Int = (remaining/1000).toInt()
                        val msg = "Starting next level in $seconds seconds"
                        txtPaused.text = msg
                    }

                    override fun onFinish() {
                        viewModel.gameModel.gameState.postValue(GameState.PLAYING)
                        Toast.makeText(this@SinglePlayerGameActivity,"Next Level Started!", Toast.LENGTH_SHORT).show()
                    }
                }.start()
            }
            GameState.FINISHED -> {

                timer.cancel()

                 if(viewModel.gameModel.player!!.isWinner){
                     showWinDialog()
                     viewModel.updateDataInFireStore()
                 }
                 else{
                     showLoseDialog()
                 }
            }
            else ->{
                val txtScore = "Score: " + viewModel.gameModel.player!!.score.toString()
                binding.txtScore1.text = txtScore
                timer.cancel()
                viewModel.gameModel.gameState.postValue(GameState.PLAYING)
            }
        }
    }

    private fun showWinDialog(){

        dlg = Dialog(this)

        val dialogBinding : DialogWinBinding =  DialogWinBinding.inflate(LayoutInflater.from(this))

        val btnOk : Button = dialogBinding.btnOkWin
        btnOk.setOnClickListener {
            dlg?.dismiss()
            finish()
        }

        dialogBinding.txtName.text = viewModel.gameModel.player!!.name
        dialogBinding.txtScore.text = viewModel.gameModel.player!!.score.toString()

        dlg?.apply {
            setContentView(dialogBinding.root)
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window!!.setLayout(
                750,
                750
            )
            window!!.setGravity(Gravity.CENTER)
        }?.show()

    }

    private fun showLoseDialog(){

        dlg = Dialog(this)

        val dialogBinding : DialogLoseBinding =  DialogLoseBinding.inflate(LayoutInflater.from(this))

        val btnOk : Button = dialogBinding.btnOkLose
        btnOk.setOnClickListener {
            dlg?.dismiss()
            finish()
        }

        dlg?.apply {
            setContentView(dialogBinding.root)
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window!!.setLayout(
                750,
                750
            )
            window!!.setGravity(Gravity.CENTER)
        }?.show()

    }

    private fun showBackDialog(){

        dlg = Dialog(this)

        val dialogBinding : DialogBackBinding =  DialogBackBinding.inflate(LayoutInflater.from(this))

        val btnOk : Button = dialogBinding.btnOkBack
        btnOk.setOnClickListener {
            dlg?.dismiss()
            finish()
        }

        val btnCancel : Button = dialogBinding.btnCancel

        btnCancel.setOnClickListener {
            dlg?.dismiss()
        }

        dlg?.apply {
            setContentView(dialogBinding.root)
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window!!.setLayout(
                750,
                750
            )
            window!!.setGravity(Gravity.CENTER)
        }?.show()
    }

    override fun onBackPressed() {
        showBackDialog()
    }
}

