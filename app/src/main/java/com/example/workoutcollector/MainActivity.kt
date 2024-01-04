package com.example.workoutcollector

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import android.content.SharedPreferences
import android.widget.CheckBox

class MainActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginbtn: Button
    private lateinit var signUpbtn: TextView
    private lateinit var fireBaseAuth: FirebaseAuth
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var rememberMeCheckbox: CheckBox
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        signUpbtn = findViewById(R.id.signup_text)
        loginbtn= findViewById(R.id.login_button)
        fireBaseAuth = FirebaseAuth.getInstance()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        password = findViewById<EditText>(R.id.password)
        email = findViewById(R.id.email)

        rememberMeCheckbox = findViewById(R.id.remember_me_checkbox)
        sharedPreferences = getSharedPreferences("WorkoutCollectorPrefs", MODE_PRIVATE)

        checkSavedCredentials()

        val intent = Intent(this@MainActivity, workoutFilterPage::class.java)

        loginbtn.setOnClickListener {

            if (email.text.isNotBlank() && password.text.isNotBlank()) {
                firebaseAnalytics.logEvent("LoginBtnClicked", null)

                val inputtedEmail: String = email.text.toString().trim()
                val inputtedPassword: String = password.text.toString().trim()

                fireBaseAuth.signInWithEmailAndPassword(inputtedEmail, inputtedPassword)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(ContentValues.TAG, "signInWithEmail:success")
                            val user = fireBaseAuth.currentUser
                            //go to view
                            intent.putExtra("Name", email.text.toString())
                            if (email.text.isNotBlank() && password.text.isNotBlank()) {
                                startActivity(intent)
                            }
                            if (rememberMeCheckbox.isChecked) {
                                saveCredentials(inputtedEmail, inputtedPassword)
                            } else {
                                clearCredentials()
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            } else{
                Toast.makeText(
                    this,
                    getString(R.string.enter_email_and_password),
                    Toast.LENGTH_SHORT,
                ).show()
            }

        }

        signUpbtn.setOnClickListener {
            val intent = Intent(this@MainActivity, signupPage::class.java)
            startActivity(intent)
        }

    }

    private fun checkSavedCredentials() {
        val savedEmail = sharedPreferences.getString("email", null)
        val savedPassword = sharedPreferences.getString("password", null)
        if (savedEmail != null && savedPassword != null) {
            email.setText(savedEmail)
            password.setText(savedPassword)
            rememberMeCheckbox.isChecked = true
        }
    }

    private fun saveCredentials(email: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }

    private fun clearCredentials() {
        val editor = sharedPreferences.edit()
        editor.remove("email")
        editor.remove("password")
        editor.apply()
    }

}
