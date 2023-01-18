package pt.isec.a21280210_a21280183_a21280330.tp_amov

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.isec.a21280210_a21280183_a21280330.tp_amov.databinding.ActivityRegisterBinding
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegisterBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.auth = Firebase.auth
        this.db = Firebase.firestore
        this.binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnSignup.setOnClickListener {

            val username = binding.inputUsername
            val email = binding.inputEmail
            val password = binding.inputPassword

            if (password.text.toString().isEmpty() || email.text.toString().isEmpty()){
                updateUI(it, null)
            }
            else{
                createUserWithEmail(username.text.toString(),email.text.toString(),password.text.toString(),it)
            }
        }

        binding.textLoginInstead.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if(currentUser != null){
            reload()
        }
    }

    private fun reload(){

    }

    private fun createUserWithEmail(username: String, email: String, password: String, view: View) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(this) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "createUserWithEmail:success")
                Snackbar.make(view, "Account created successfully", Snackbar.LENGTH_SHORT)
                    .show()

                val user = auth.currentUser

                val profileUpdates = userProfileChangeRequest {
                    displayName = username
                    photoUri = Uri.parse("https://example.com/jane-q-user/profile.jpg")
                }

                user!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "User profile updated.")
                        }
                    }

                val date = Calendar.getInstance().time

                val newUser = hashMapOf(
                    "id" to user.uid,
                    "username" to username,
                    "email" to email,
                    "date_created" to date
                )

                db.collection("User").document(user.uid)
                    .set(newUser)
                    .addOnSuccessListener {
                        Log.d(
                            tagCF,
                            "New user document successfully written!"
                        )
                    }
                    .addOnFailureListener { Log.d(tagCF, "Error writing user document.") }
                updateUI(view, user)

            }
            .addOnFailureListener { e ->
                // If sign in fails, display a message to the user.
                Log.i(TAG, "createUserWithEmail:failure ${e.message}")
                Snackbar.make(view, "${e.message}",
                    Snackbar.LENGTH_SHORT).show()

            }
    }

    private fun updateUI(view: View ,user: FirebaseUser?) {

        if(user != null){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
        else Snackbar.make(view, "Please check if you filled every field correctly.", Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "EmailPassword"
        private const val tagCF = "CloudFirestore"
    }
}