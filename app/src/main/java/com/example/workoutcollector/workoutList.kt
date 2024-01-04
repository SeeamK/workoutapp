package com.example.workoutcollector

import WorkoutAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class workoutList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_list)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        val intent = intent
        val name = intent.getStringExtra("Name") ?: ""

        val wManager = workoutManager(this)


        CoroutineScope(IO).launch {
            try {
                val listOfWorkouts = wManager.retrieveTargetWorkouts(name)
                withContext(Main) {
                    if(listOfWorkouts.isEmpty()){
                        val builder = AlertDialog.Builder(this@workoutList)
                        builder.setTitle(getString(R.string.noneFound))
                        builder.setMessage(getString(R.string.noWorkoutsInDay))
                        builder.setIcon(R.drawable.musclemanicon)
                        builder.setOnDismissListener {
                            val accountIntent = Intent(this@workoutList, accountpage::class.java)
                            startActivity(accountIntent)
                        }
                        val dialog = builder.create()
                        dialog.show()
                    } else{
                        val recyclerView = findViewById<RecyclerView>(R.id.resultsRecycler)
                        recyclerView.layoutManager = LinearLayoutManager(this@workoutList)
                        recyclerView.adapter = WorkoutAdapter(this@workoutList, listOfWorkouts, name)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace() // Log the exception
                // Handle any errors here, such as showing a Toast or Snackbar
            } finally {
                withContext(Main) {
                    progressBar.visibility = View.GONE // Hide ProgressBar in the UI thread
                }
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val homeIntent = Intent(this@workoutList, workoutFilterPage::class.java)
                    startActivity(homeIntent)
                    true
                }
                R.id.navigation_account -> {
                    val accountIntent = Intent(this@workoutList, accountpage::class.java)
                    startActivity(accountIntent)
                    true
                }
                else -> false
            }
        }
    }
}
