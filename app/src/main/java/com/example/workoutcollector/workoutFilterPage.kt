package com.example.workoutcollector

import FilterAdapter
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

class workoutFilterPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_filter_page)


        //fill target muscle view
        val recyclerView = findViewById<RecyclerView>(R.id.targetMuscleFilter)

        // Create a GridLayoutManager with horizontal orientation and 2 rows
        val layoutManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)

        // Set the layoutManager to the recyclerView
        recyclerView.layoutManager = layoutManager


        // Create a list of FilterItems
        val filterItems = listOf(
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.abductors)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.abs)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.adductors)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.biceps)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.calves)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.cardiovascular_system)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.delts)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.forearms)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.glutes)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.hamstrings)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.lats)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.levator_scapulae)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.pectorals)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.quads)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.serratus_anterior)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.spine)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.traps)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.triceps)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.upper_back))
        )


        // Create an instance of the adapter and set it to the RecyclerView
        recyclerView.adapter = FilterAdapter(filterItems, this)
        //

        //fill equipment choice view
        val recyclerView2 = findViewById<RecyclerView>(R.id.equipmentFilter)
        // Create a GridLayoutManager with horizontal orientation and 2 rows
        val layoutManager2 = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)

        // Set the layoutManager to the recyclerView
        recyclerView.layoutManager = layoutManager2

        // Create a list of FilterItems
        val filterItems2 = listOf(
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.assisted)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.band)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.barbell)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.body_weight)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.bosu_ball)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.cable)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.dumbbell)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.elliptical_machine)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.ez_barbell)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.hammer)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.kettle_bell)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.leverage_machine)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.medicine_ball)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.olympic_barbell)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.resistance_band)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.roller)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.rope)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.skierg_machine)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.sled_machine)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.smith_machine)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.stability_ball)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.stationary_bike)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.stepmill_machine)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.tire)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.trap_bar)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.upper_body_ergometer)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.weighted)),
            FilterItem(R.mipmap.ic_launcher_round, getString(R.string.wheel_roller))
        )

        // Create an instance of the adapter and set it to the RecyclerView
        recyclerView2.adapter = FilterAdapter(filterItems2, this)
        //



        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@workoutFilterPage, workoutFilterPage::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_account -> {
                    val intent = Intent(this@workoutFilterPage, accountpage::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }

        }
    }

}