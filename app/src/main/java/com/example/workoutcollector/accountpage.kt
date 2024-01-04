package com.example.workoutcollector

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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


class accountpage : AppCompatActivity() {

    private lateinit var favBtn: Button
    private lateinit var monBtn: Button
    private lateinit var tuesBtn: Button
    private lateinit var wedBtn: Button
    private lateinit var thurBtn: Button
    private lateinit var friBtn: Button
    private lateinit var satBtn: Button
    private lateinit var sunBtn: Button
    private lateinit var fireBaseAuth: FirebaseAuth
    private lateinit var welcomText: TextView
    private lateinit var signout: FloatingActionButton
    private lateinit var noti: Switch
    private lateinit var sharedPreferences: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accountpage)

        welcomText = findViewById(R.id.welcomeText)
        monBtn = findViewById(R.id.monBtn)
        tuesBtn = findViewById(R.id.tuesBtn)
        wedBtn = findViewById(R.id.wedBtn)
        thurBtn = findViewById(R.id.thursBtn)
        friBtn = findViewById(R.id.fribtn)
        satBtn = findViewById(R.id.satBtn)
        sunBtn = findViewById(R.id.sunBtn)
        favBtn = findViewById(R.id.favorites)
        signout = findViewById(R.id.signoutBtn)
        noti = findViewById(R.id.notificationSwitch)
        sharedPreferences = getSharedPreferences("SwitchState", MODE_PRIVATE)
        createNotificationChannel()

        fireBaseAuth = FirebaseAuth.getInstance()
        val user = fireBaseAuth.currentUser

        if (user != null) {
            welcomText.text = getString(R.string.welcome2) + user.displayName.toString()
        }

        val workoutsTdy = findViewById<TextView>(R.id.workoutstdy)
        val img = findViewById<ImageView>(R.id.workoutGif)
        val progbar = findViewById<ProgressBar>(R.id.progressBar2)

        val date = LocalDate.now()
        val currentDay = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        val database = Firebase.database
        val databaseReference = database.getReference("Users/${user?.uid}")

        // Check if there are children for the current day in the database
        databaseReference.child(currentDay).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progbar.visibility = View.GONE
                if (snapshot.exists()) {
                    println("workouts in day")
                    workoutsTdy.text = getString(R.string.you_got_workouts_to_do)
                    Glide.with(this@accountpage)
                        .asGif()
                        .load(R.drawable.zoro)
                        .into(img)
                } else{
                    workoutsTdy.text = getString(R.string.no_workouts_for_today)
                    Glide.with(this@accountpage)
                        .asGif()
                        .load(R.drawable.stanlybored)
                        .into(img)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("fail in account page data retrieval")
            }
        })

        // Retrieve the last saved switch state, defaulting to false if not found
        val isSwitchOn = sharedPreferences.getBoolean("isSwitchOn", false)
        noti.isChecked = isSwitchOn
        noti.setOnCheckedChangeListener { _, isChecked ->
            // Save the switch state when it changes
            sharedPreferences.edit().putBoolean("isSwitchOn", isChecked).apply()
            if (isChecked) {
                val notification = NotificationCompat.Builder(this, "default")
                    .setContentTitle("Notifications are now enabled")
                    .setContentText("You will receive a reminder for days that you have workouts saved")
                    .setSmallIcon(R.drawable.muscleman)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                with(NotificationManagerCompat.from(this)) {
                    // notificationId is a unique int for each notification that you must define.
                    if (ActivityCompat.checkSelfPermission(
                            this@accountpage,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this@accountpage,
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            200
                        )
                        return@setOnCheckedChangeListener
                    }
                    notify(1, notification.build())
                }

            } else{
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val notificationIntent = Intent(this, NotificationReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
                )
                // Cancel the alarm
                alarmManager.cancel(pendingIntent)
                Toast.makeText(this, "Notifications now disabled", Toast.LENGTH_SHORT).show()
            }
        }

        if(isSwitchOn){//make so they recive noti if day has children
            val date = LocalDate.now()
            val currentDay = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
            println("day = $currentDay")
            val user = fireBaseAuth.currentUser
            val database = Firebase.database
            val databaseReference = database.getReference("Users/${user?.uid}")

            // Check if there are children for the current day in the database
            databaseReference.child(currentDay).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        println("workouts in day")
                        // Children exist for the current day, show notification
                        scheduleNotification()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("fail in account page data retrieval")
                }
            })
        }



        signout.setOnClickListener {
            fireBaseAuth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            // Start the activity using the context
            startActivity(intent)
        }

        monBtn.setOnClickListener{
            val intent = Intent(this, workoutList::class.java).apply {
                // Pass the filter name as an extra
                putExtra("Name", "Monday")
            }
            // Start the activity using the context
            startActivity(intent)
        }

        tuesBtn.setOnClickListener{
            val intent = Intent(this, workoutList::class.java).apply {
                // Pass the filter name as an extra
                putExtra("Name", "Tuesday")
            }
            // Start the activity using the context
            startActivity(intent)
        }

        wedBtn.setOnClickListener{
            val intent = Intent(this, workoutList::class.java).apply {
                // Pass the filter name as an extra
                putExtra("Name", "Wednesday")
            }
            // Start the activity using the context
            startActivity(intent)
        }

        thurBtn.setOnClickListener{
            val intent = Intent(this, workoutList::class.java).apply {
                // Pass the filter name as an extra
                putExtra("Name", "Thursday")
            }
            // Start the activity using the context
            startActivity(intent)
        }

        friBtn.setOnClickListener{
            val intent = Intent(this, workoutList::class.java).apply {
                // Pass the filter name as an extra
                putExtra("Name", "Friday")
            }
            // Start the activity using the context
            startActivity(intent)
        }

        satBtn.setOnClickListener{
            val intent = Intent(this, workoutList::class.java).apply {
                // Pass the filter name as an extra
                putExtra("Name", "Saturday")
            }
            // Start the activity using the context
            startActivity(intent)
        }

        sunBtn.setOnClickListener{
            val intent = Intent(this, workoutList::class.java).apply {
                // Pass the filter name as an extra
                putExtra("Name", "Sunday")
            }
            // Start the activity using the context
            startActivity(intent)
        }

        favBtn.setOnClickListener{
            val intent = Intent(this, workoutList::class.java).apply {
                // Pass the filter name as an extra
                putExtra("Name", "favorites")
            }
            // Start the activity using the context
            startActivity(intent)
        }



        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@accountpage, workoutFilterPage::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_account -> {
                    val intent = Intent(this@accountpage, accountpage::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }

        }

    }

    private fun scheduleNotification() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                setAlarm(alarmManager)
            }
        } else {
            setAlarm(alarmManager) // For lower API levels
        }
    }

    private fun setAlarm(alarmManager: AlarmManager) {
        val notificationIntent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val timeToNotify = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 35)
        }

        try {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                timeToNotify.timeInMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            // Handle the exception for API 31 and higher
            // You could show a message to the user or log the error
            Log.d("setAlarm", "catch exception: $e")
        }
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "workoutNoti"
            val channelName = "Workout Notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val descriptionText = "Channel for Workout Reminders"

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


}