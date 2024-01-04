package com.example.workoutcollector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class workoutInfo : AppCompatActivity() {

    private lateinit var name: TextView
    private lateinit var target: TextView
    private lateinit var equipment: TextView
    private lateinit var gif: ImageView
    private lateinit var instructionList: ListView // make sure this matches the ID in your XML
    private lateinit var favBtn: FloatingActionButton
    private lateinit var routineBtn: FloatingActionButton
    private lateinit var shareBtn: FloatingActionButton
    private lateinit var fireBaseAuth: FirebaseAuth

    private lateinit var workout: workoutItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_info)

        fireBaseAuth = FirebaseAuth.getInstance()
        favBtn = findViewById(R.id.addToFavBtn)
        routineBtn = findViewById(R.id.addToRoutineBtn)
        shareBtn = findViewById(R.id.shareBtn)
        name = findViewById(R.id.wname)
        target = findViewById(R.id.wtarget)
        equipment = findViewById(R.id.wequipment)
        gif = findViewById(R.id.workout_gif)
        instructionList =
            findViewById(R.id.instructionList) // Ensure this ID matches your ListView in XML

        val progressBar = findViewById<ProgressBar>(R.id.progressBarInfo)
        progressBar.visibility = View.VISIBLE

        val intent = intent
        val workout_id = intent.getStringExtra("id")
        println("id: ${workout_id}")

        CoroutineScope(IO).launch {
            try {
                workout_id?.let {
                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url("https://exercisedb.p.rapidapi.com/exercises/exercise/$it")
                        .get()
                        .addHeader("X-RapidAPI-Key", resources.getString(R.string.api_key))
                        .addHeader("X-RapidAPI-Host", "exercisedb.p.rapidapi.com")
                        .build()

                    val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    println("HTTP Request successful: ${response.code}")
                    val responseBody: String? = response.body?.string()
                    val gson = Gson()
                    // Directly parse into workoutItem
                    workout = gson.fromJson(responseBody, workoutItem::class.java)

                    withContext(Main) {
                        // Update the UI with the single workoutItem
                        name.text = workout.name
                        target.text = getString(R.string.target_muscles, workout.target)
                        equipment.text = getString(R.string.equipment_needed, workout.equipment)
                        Glide.with(this@workoutInfo)
                            .asGif()
                            .load(workout.gifUrl)
                            .into(gif)
                        val instructionsAdapter = ArrayAdapter(
                            this@workoutInfo,
                            android.R.layout.simple_list_item_1,
                            workout.instructions // This assumes instructions is a List<String>
                        )
                        instructionList.adapter = instructionsAdapter
                    }
                } else {
                    // Log error
                    println("HTTP Request not successful: ${response.code}")
                }
                } ?: println("Error: workout_id is null or blank")
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                withContext(Main) {
                    progressBar.visibility = View.GONE // Hide ProgressBar in the UI thread
                }
            }
        }


        favBtn.setOnClickListener {
            val user = fireBaseAuth.currentUser
            val database = Firebase.database
            val myRef = database.getReference("Users/${user?.uid}/favorites")

            // Check if workout_id already exists
            myRef.orderByValue().equalTo(workout_id).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // If workout_id exists, show a toast message and do not add to favorites again
                        Toast.makeText(this@workoutInfo, "Workout already in Favorites", Toast.LENGTH_SHORT).show()
                    } else {
                        // If workout_id does not exist, add it to favorites
                        myRef.push().setValue(workout_id).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this@workoutInfo, "${name.text} Added to Favorites", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@workoutInfo, "Sorry, it looks like something went wrong", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors
                    Toast.makeText(this@workoutInfo, "Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }


        shareBtn.setOnClickListener{
            shareWorkout()
        }


        routineBtn.setOnClickListener {
            val user = fireBaseAuth.currentUser
            if (user != null && workout_id != null) {
                // Define the options
                val options = arrayOf(getString(R.string.monday),
                    getString(R.string.tuesday),
                    getString(R.string.wednesday),
                    getString(R.string.thursday), getString(R.string.friday),
                    getString(R.string.saturday), getString(R.string.sunday))
                // Create an AlertDialog Builder
                val builder = AlertDialog.Builder(this@workoutInfo)
                builder.setTitle(getString(R.string.choose_day_to_add_workout_to))
                builder.setItems(options) { dialog, which ->
                    // 'which' is the index position of the selected item
                    val selectedDay = options[which]

                    // Get a reference to the user's routine for the selected day
                    val database = Firebase.database
                    val myRef = database.getReference("Users/${user.uid}/$selectedDay")

                    // Push the workout_id to the selected day's routine
                    myRef.push().setValue(workout_id).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@workoutInfo,
                                getString(R.string.workout_added_to_routine, selectedDay), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@workoutInfo,
                                getString(R.string.failed_to_add_workout), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                // Create and show the AlertDialog
                val dialog = builder.create()
                dialog.show()
            } else {
                // Handle the case where user or workout_id is null
                Toast.makeText(this,
                    getString(R.string.user_not_logged_in_or_invalid_workout_id), Toast.LENGTH_SHORT).show()
            }
        }



        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@workoutInfo, workoutFilterPage::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_account -> {
                    val intent = Intent(this@workoutInfo, accountpage::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }

        }

    }

    private fun shareWorkout() {
        val workoutText = formatWorkout(workout) // Convert the workout object to a String

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, workoutText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Share This Workout")
        startActivity(shareIntent)
    }

    private fun formatWorkout(workout: workoutItem): String {
        val gson = Gson()
        return formatJsonToUserFriendlyString(gson.toJson(workout))
    }

    private fun formatJsonToUserFriendlyString(jsonString: String): String {
        val jsonObject = JSONObject(jsonString)

        val bodyPart = jsonObject.getString("bodyPart")
        val equipment = jsonObject.getString("equipment")
        val gifUrl = jsonObject.getString("gifUrl")
        val name = jsonObject.getString("name")
        val target = jsonObject.getString("target")

        val instructionsArray = jsonObject.getJSONArray("instructions")
        val instructions = (0 until instructionsArray.length()).joinToString("\n") {
            "- ${instructionsArray.getString(it)}"
        }

        return "Workout: $name\n" +
                "Body Part: $bodyPart\n" +
                "Target: $target\n" +
                "Equipment: $equipment\n" +
                "Instructions:\n$instructions\n" +
                "GIF URL: $gifUrl"
    }

}





