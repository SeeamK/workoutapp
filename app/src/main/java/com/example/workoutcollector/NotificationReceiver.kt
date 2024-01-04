package com.example.workoutcollector

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val notification = NotificationCompat.Builder(context, "workoutNoti")
            .setContentTitle("Workout Reminder")
            .setContentText("Time for your workout!")
            .setSmallIcon(R.drawable.musclemanicon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(1001, notification)
        }
    }
}
