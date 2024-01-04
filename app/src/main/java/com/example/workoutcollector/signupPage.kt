package com.example.workoutcollector

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest

class signupPage : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var Repassword: EditText
    private lateinit var name: EditText
    private lateinit var signUp: Button
    private lateinit var fireBaseAuth: FirebaseAuth
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_page)

        email = findViewById(R.id.signemail)
        password = findViewById(R.id.signpassword)
        Repassword = findViewById(R.id.passwordReenter)
        name = findViewById(R.id.name)
        signUp = findViewById(R.id.signup_button)
        fireBaseAuth = FirebaseAuth.getInstance()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)


        val intent = Intent(this@signupPage, MainActivity::class.java)

        signUp.setOnClickListener{
            val inputtedEmail: String = email.text.toString().trim()
            val inputtedPassword: String = password.text.toString().trim()
            val inputtedRePassword: String = Repassword.text.toString().trim()
            val inputtedname: String = name.text.toString().trim()
            if(inputtedPassword != inputtedRePassword){
                Toast.makeText(this, getString(R.string.passNotmatch), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(inputtedEmail.isNullOrEmpty() || inputtedname.isNullOrEmpty() || inputtedPassword.isNullOrEmpty() || inputtedRePassword.isNullOrEmpty()){
                Toast.makeText(this, getString(R.string.emptyFields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            fireBaseAuth.createUserWithEmailAndPassword(inputtedEmail, inputtedPassword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(ContentValues.TAG, "createUserWithEmail:success")
                        Toast.makeText(this, getString(R.string.user_created), Toast.LENGTH_SHORT).show()
                        val user = fireBaseAuth.currentUser
                        //set users name
                        val profileUpdates = userProfileChangeRequest {
                            displayName = inputtedname
                        }

                        user!!.updateProfile(profileUpdates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "User profile updated.")
                                }
                            }
                        Toast.makeText(
                            this,
                            getString(R.string.please_login),
                            Toast.LENGTH_LONG,
                        ).show()
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            getString(R.string.authentication_failed),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }

    }
}