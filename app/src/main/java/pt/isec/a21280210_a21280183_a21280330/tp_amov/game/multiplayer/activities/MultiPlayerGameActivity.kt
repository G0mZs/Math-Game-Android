package pt.isec.a21280210_a21280183_a21280330.tp_amov.game.multiplayer.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputFilter
import android.text.Spanned
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import pt.isec.a21280210_a21280183_a21280330.tp_amov.R
import pt.isec.a21280210_a21280183_a21280330.tp_amov.data.ConnectionState
import pt.isec.a21280210_a21280183_a21280330.tp_amov.data.GameState
import pt.isec.a21280210_a21280183_a21280330.tp_amov.data.Player
import pt.isec.a21280210_a21280183_a21280330.tp_amov.databinding.ActivityMultiplayerGameBinding
import pt.isec.a21280210_a21280183_a21280330.tp_amov.databinding.DialogMultiplayerFinishBinding
import pt.isec.a21280210_a21280183_a21280330.tp_amov.game.GameModel.SERVER_PORT
import pt.isec.a21280210_a21280183_a21280330.tp_amov.game.multiplayer.MultiPlayerGameViewModel
import pt.isec.a21280210_a21280183_a21280330.tp_amov.game.multiplayer.MultiPlayerGridAdapter
import pt.isec.a21280210_a21280183_a21280330.tp_amov.game.multiplayer.OnSwipeTouchMpListener


class MultiPlayerGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMultiplayerGameBinding
    private lateinit var viewModel: MultiPlayerGameViewModel
    private var dlg: AlertDialog? = null
    private lateinit var txtPaused : TextView
    private lateinit var txtScore : TextView
    private lateinit var txtInfo : TextView
    private var list : ListView? = null
    private lateinit var myGridAdapter: MultiPlayerGridAdapter
    private lateinit var gridView: GridView
    private var counter = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultiplayerGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        gridView = binding.gridMp
        txtInfo = binding.txtInfo
        txtScore = binding.score
        txtPaused = binding.txtPause
        txtPaused.visibility = View.INVISIBLE
        viewModel = ViewModelProvider(this)[MultiPlayerGameViewModel::class.java]

        if(intent.getIntExtra("mode", SERVER_MODE) == CLIENT_MODE){
            viewModel.gameModel.initializeLevel()
        }

        myGridAdapter = MultiPlayerGridAdapter(viewModel, this)
        gridView.adapter = myGridAdapter

        gridView.setOnTouchListener(object : OnSwipeTouchMpListener(this@MultiPlayerGameActivity,gridView,viewModel,intent){})

        viewModel.connectionState.observe(this){

            when (it) {
                ConnectionState.CONNECTION_ESTABLISHED -> {
                    when(intent.getIntExtra("mode", SERVER_MODE)){
                        SERVER_MODE -> {

                            dlg?.dismiss()
                            dlg = null

                            val ll = LinearLayout(this).apply {
                                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                this.setPadding(50, 50, 50, 50)
                                layoutParams = params
                                setBackgroundColor(Color.rgb(160, 226, 250))
                                orientation = LinearLayout.HORIZONTAL
                                addView(ProgressBar(context).apply {
                                    isIndeterminate = true
                                    val paramsPB = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                    paramsPB.gravity = Gravity.CENTER_VERTICAL
                                    layoutParams = paramsPB
                                    indeterminateTintList = ColorStateList.valueOf(Color.rgb(96, 96, 32))
                                })
                                addView(TextView(context).apply {
                                    val paramsTV = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                    layoutParams = paramsTV
                                    text = String.format("Number of players connected : ${viewModel.gameModel.players.size - 1}. Press Ok to start the game or await any other connections")
                                    textSize = 20f
                                    setTextColor(Color.rgb(96, 96, 32))
                                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                                })
                            }

                            dlg = AlertDialog.Builder(this)
                                .setTitle(R.string.server_mode)
                                .setView(ll)
                                .setOnCancelListener {
                                    viewModel.stopServer()
                                    finish()
                                }
                                .setPositiveButton("OK"){ _: DialogInterface, _: Int ->
                                    viewModel.startGame()
                                }
                                .create()

                            dlg?.show()
                        }
                        CLIENT_MODE -> {

                            dlg?.dismiss()
                            dlg = null

                            val ll = LinearLayout(this).apply {
                                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                this.setPadding(50, 50, 50, 50)
                                layoutParams = params
                                setBackgroundColor(Color.rgb(160, 226, 250))
                                orientation = LinearLayout.HORIZONTAL
                                addView(ProgressBar(context).apply {
                                    isIndeterminate = true
                                    val paramsPB = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                    paramsPB.gravity = Gravity.CENTER_VERTICAL
                                    layoutParams = paramsPB
                                    indeterminateTintList = ColorStateList.valueOf(Color.rgb(96, 96, 32))
                                })
                                addView(TextView(context).apply {
                                    val paramsTV = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                    layoutParams = paramsTV
                                    text = String.format("Waiting for server to start the game...")
                                    textSize = 20f
                                    setTextColor(Color.rgb(96, 96, 32))
                                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                                })
                            }

                            dlg = AlertDialog.Builder(this)
                                .setTitle(R.string.client_mode)
                                .setView(ll)
                                .setCancelable(false)
                                .create()

                            dlg?.show()

                        }
                    }
                }
                ConnectionState.SETTING_PARAMETERS -> {
                    when (intent.getIntExtra("mode", SERVER_MODE)) {
                        SERVER_MODE -> startAsServer()
                        CLIENT_MODE -> startAsClient()
                    }
                }
                else -> {}
            }


        }

        viewModel.gameModel.gameState.observe(this){

            when(it){
                GameState.PLAYING -> {
                    txtInfo.visibility = View.INVISIBLE
                    counter++
                    if(counter > 1){
                        gridView.visibility = View.INVISIBLE
                        object : CountDownTimer(5000, 1000) {

                            override fun onTick(remaining: Long) {
                                txtPaused.visibility = View.VISIBLE
                                val seconds : Int = (remaining/1000).toInt()
                                val msg = "Starting next level in $seconds seconds"
                                txtPaused.text = msg

                            }

                            override fun onFinish() {
                                Toast.makeText(this@MultiPlayerGameActivity,"Next Level Started!", Toast.LENGTH_SHORT).show()
                                val txtLevel = "LEVEL " + viewModel.gameModel.level.number.toString()
                                binding.level.text = txtLevel

                                myGridAdapter.notifyDataSetChanged()
                                gridView.visibility = View.VISIBLE
                                txtPaused.visibility = View.INVISIBLE
                                val score = "Score: " + viewModel.getScore().toString()
                                txtScore.text = score
                            }
                        }.start()

                    }else{

                        if(dlg?.isShowing == true){
                            dlg?.dismiss()
                            dlg = null
                        }
                        val txtLevel = "LEVEL " + viewModel.gameModel.level.number.toString()
                        binding.level.text = txtLevel

                        myGridAdapter.notifyDataSetChanged()
                        gridView.visibility = View.VISIBLE

                        val score = "Score: 0"
                        txtScore.text = score
                    }
                }
                GameState.CORRECT_ANSWER -> {
                    Toast.makeText(this,"Correct Answer: +2 points",Toast.LENGTH_SHORT).show()
                    myGridAdapter.notifyDataSetChanged()
                    val score = "Score: " + viewModel.getScore().toString()
                    txtScore.text = score
                }
                GameState.SECOND_CORRECT_ANSWER -> {
                    Toast.makeText(this,"2nd Correct Answer: +1 points",Toast.LENGTH_SHORT).show()
                    myGridAdapter.notifyDataSetChanged()
                    val score = "Score: " + viewModel.getScore().toString()
                    txtScore.text = score
                }
                GameState.WRONG_ANSWER -> {
                    Toast.makeText(this,"Wrong Answer !",Toast.LENGTH_SHORT).show()
                    myGridAdapter.notifyDataSetChanged()
                }
                GameState.PAUSED -> {
                    val text = "Level completed sucessfuly!"
                    txtInfo.text = text
                    txtInfo.visibility = View.VISIBLE
                    gridView.visibility = View.INVISIBLE
                    Toast.makeText(this,"Level Concluded !",Toast.LENGTH_SHORT).show()
                    txtPaused.visibility = View.INVISIBLE
                    val score = "Score: " + viewModel.getScore().toString()
                    txtScore.text = score

                }
                GameState.LOST -> {
                    val text = "You Lost the Game :("
                    txtInfo.text = text
                    txtInfo.visibility = View.VISIBLE
                    gridView.visibility = View.INVISIBLE

                }
                GameState.FINISHED -> {
                    Toast.makeText(this,"GAME FINISHED",Toast.LENGTH_SHORT).show()
                    showFinishScreen()
                }
                else -> {}
            }
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        viewModel.gameModel.reset()
    }

    private fun startAsServer() {
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val ip = wifiManager.connectionInfo.ipAddress // Deprecated in API Level 31. Suggestion NetworkCallback
        val strIPAddress = String.format("%d.%d.%d.%d",
            ip and 0xff,
            (ip shr 8) and 0xff,
            (ip shr 16) and 0xff,
            (ip shr 24) and 0xff
        )

        val ll = LinearLayout(this).apply {
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            this.setPadding(50, 50, 50, 50)
            layoutParams = params
            setBackgroundColor(Color.rgb(160, 226, 250))
            orientation = LinearLayout.HORIZONTAL
            addView(ProgressBar(context).apply {
                isIndeterminate = true
                val paramsPB = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                paramsPB.gravity = Gravity.CENTER_VERTICAL
                layoutParams = paramsPB
                indeterminateTintList = ColorStateList.valueOf(Color.rgb(96, 96, 32))
            })
            addView(TextView(context).apply {
                val paramsTV = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                layoutParams = paramsTV
                text = String.format(getString(R.string.msg_ip_address),strIPAddress)
                textSize = 20f
                setTextColor(Color.rgb(96, 96, 32))
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            })
        }

        dlg = AlertDialog.Builder(this)
            .setTitle(R.string.server_mode)
            .setView(ll)
            .setOnCancelListener {
                viewModel.stopServer()
                finish()
            }
            .create()

        viewModel.startServer()

        dlg?.show()
    }

    private fun startAsClient() {
        val edtBox = EditText(this).apply {
            maxLines = 1
            filters = arrayOf(object : InputFilter {
                override fun filter(
                    source: CharSequence?,
                    start: Int,
                    end: Int,
                    dest: Spanned?,
                    dstart: Int,
                    dend: Int
                ): CharSequence? {
                    source?.run {
                        var ret = ""
                        forEach {
                            if (it.isDigit() || it == '.')
                                ret += it
                        }
                        return ret
                    }
                    return null
                }

            })
        }
        val dlg = AlertDialog.Builder(this)
            .setTitle(R.string.client_mode)
            .setMessage(R.string.ask_ip)
            .setPositiveButton(R.string.button_connect) { _: DialogInterface, _: Int ->
                val strIP = edtBox.text.toString()
                if (strIP.isEmpty() || !Patterns.IP_ADDRESS.matcher(strIP).matches()) {
                    Toast.makeText(this@MultiPlayerGameActivity, R.string.error_address, Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    viewModel.startClient(strIP)
                }
            }
            .setNeutralButton(R.string.btn_emulator) { _: DialogInterface, _: Int ->
                viewModel.startClient("10.0.2.2", SERVER_PORT-1)

            }
            .setNegativeButton(R.string.button_cancel) { _: DialogInterface, _: Int ->
                finish()
            }
            .setCancelable(false)
            .setView(edtBox)
            .create()

        dlg.show()
    }

    private fun showFinishScreen(){

        val dialogBinding : DialogMultiplayerFinishBinding =  DialogMultiplayerFinishBinding.inflate(LayoutInflater.from(this))
        setContentView(dialogBinding.root)

        val players = ArrayList<String>()
        val text : TextView = dialogBinding.txtTimer

        for(player : Player in viewModel.gameModel.players) {
            if (player.onGame) {
                val string = "Player: ${player.name} - Score: ${player.score} - Game Completed "
                players.add(string)
            } else {
                val string = "Player: ${player.name} - Score: ${player.score} - Game Lost"
                players.add(string)
            }
        }

        dialogBinding.finishLayout.setBackgroundColor(Color.parseColor("#FF018786"))

        list = dialogBinding.list
        val arr: ArrayAdapter<String> = ArrayAdapter<String>(
            this,android.R.layout.simple_list_item_1,players
        )
        list!!.adapter = arr

        object : CountDownTimer(15000, 1000) {

            override fun onTick(remaining: Long) {
                val seconds : Int = (remaining/1000).toInt()
                val msg = "Redirecting to Menu in $seconds s"
                text.text = msg
            }

            override fun onFinish() {
                viewModel.gameModel.reset()
                finish()
            }
        }.start()
    }

    companion object{

        private const val SERVER_MODE = 0
        private const val CLIENT_MODE = 1

        fun getServerModeIntent(context : Context) : Intent {
            return Intent(context,MultiPlayerGameActivity::class.java).apply {
                putExtra("mode", SERVER_MODE)
            }
        }

        fun getClientModeIntent(context : Context) : Intent {
            return Intent(context,MultiPlayerGameActivity::class.java).apply {
                putExtra("mode", CLIENT_MODE)
            }
        }
    }
}