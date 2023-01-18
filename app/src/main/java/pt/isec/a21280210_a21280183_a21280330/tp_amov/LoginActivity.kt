package pt.isec.a21280210_a21280183_a21280330.tp_amov

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import pt.isec.a21280210_a21280183_a21280330.tp_amov.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.auth = Firebase.auth
        this.binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.textRegisterInstead.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.inputEmail
            val password = binding.inputPassword
            if (password.text.toString().isEmpty() || email.text.toString().isEmpty()){
                updateUI(it, null)
            }
            else {
                createUserWithEmail(email.text.toString(),password.text.toString(),it)
            }
        }

    }

    private fun createUserWithEmail(email: String, password: String, view: View){

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(this) {
                Log.d(TAG, "signInWithEmail: success")
                Toast.makeText(
                    baseContext, "Authentication Successful !",
                    Toast.LENGTH_SHORT
                ).show()
                val user = auth.currentUser
                updateUI(view, user)
            }
            .addOnFailureListener(this) { e->
                Log.d(TAG, "signInWithEmail: failure ${e.message}")
                Toast.makeText(
                    baseContext, "${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun updateUI(view: View, user: FirebaseUser?) {

        if(user != null){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
        else Snackbar.make(view, "Couldn't sign in with the provided information.", Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "SignInEmailPassword"
    }

}