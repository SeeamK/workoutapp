package com.example.workoutcollector

import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class workoutManager(private val context: Context) {

    val okHttpClient: OkHttpClient

    init {
        val builder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)
        okHttpClient = builder.build()
    }

    suspend fun retrieveTargetWorkouts(name: String): MutableList<workoutItem>{

        val targets = listOf<String>("abductors","abs","adductors","biceps","calves","cardiovascular system","delts","forearms","glutes","hamstrings","lats","levator scapulae","pectorals","quads","serratus anterior","spine","traps","triceps","upper back")
        val equipments = listOf<String>("assisted","band","barbell","body weight","bosu ball","cable","dumbbell","elliptical machine","ez barbell","hammer","kettlebell","leverage machine","medicine ball","olympic barbell","resistance band","roller","rope","skierg machine","sled machine","smith machine","stability ball","stationary bike","stepmill machine","tire","trap bar","upper body ergometer","weighted","wheel roller")
        val accountChoices = listOf<String>("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday", "favorites")

        println("name: $name")

        val client = OkHttpClient()
        var request: Request

        if(targets.contains(name.lowercase())){
            println("in target if")
            val apiKey = context.getString(R.string.api_key)
            println("apikey: " + apiKey)
            request = Request.Builder()
                .url("https://exercisedb.p.rapidapi.com/exercises/target/${name.lowercase()}?limit=100")
                .get()
                .addHeader("X-RapidAPI-Key", apiKey)
                .addHeader("X-RapidAPI-Host", "exercisedb.p.rapidapi.com")
                .build()
        } else if(equipments.contains(name.lowercase())){
            println("in equipment else")
            val apiKey = context.getString(R.string.api_key)
            request = Request.Builder()
                .url("https://exercisedb.p.rapidapi.com/exercises/equipment/${name.lowercase()}?limit=100")
                .get()
                .addHeader("X-RapidAPI-Key", apiKey)
                .addHeader("X-RapidAPI-Host", "exercisedb.p.rapidapi.com")
                .build()
        } else{//in choices from account page
            // In choices from account page, fetching from Firebase
            val workoutList = mutableListOf<workoutItem>()
            val fireBaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
            val user = fireBaseAuth.currentUser
            val database = Firebase.database
            val databaseReference = database.getReference("Users/${user?.uid}/$name")

            // Suspend coroutine until data is fetched
            val dataSnapshot = suspendCoroutine<DataSnapshot> { continuation ->
                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        continuation.resume(snapshot)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }
                })
            }

            if (dataSnapshot.exists()) {
                for (childSnapshot in dataSnapshot.children) {
                    val dbWorkoutId = childSnapshot.getValue(String::class.java) ?: continue
                    val workout = fetchWorkoutDetails(dbWorkoutId)
                    workout?.let { workoutList.add(it) }
                }
            }

            return workoutList
        }

        val response = client.newCall(request).execute()
        val responseBody: String? = response.body?.string()
        println("Response body: $responseBody")
        // Assuming 'jsonResponse' is a String containing your JSON data.
        val gson = Gson()

        // Use object : TypeToken<List<workoutItem>>(){} to get the correct Type for Gson parsing
        val workoutType = object : TypeToken<List<workoutItem>>(){}.type

        // Parse the JSON response into a List of workoutItem objects
        val workoutList: MutableList<workoutItem> = gson.fromJson(responseBody, workoutType)

        // Now, `workoutList` contains all your workout items from the JSON response
        return workoutList
    }

    private suspend fun fetchWorkoutDetails(workoutId: String): workoutItem? = withContext(Dispatchers.IO) {
        try {
            val apiKey = context.getString(R.string.api_key)
            val request = Request.Builder()
                .url("https://exercisedb.p.rapidapi.com/exercises/exercise/$workoutId")
                .get()
                .addHeader("X-RapidAPI-Key", apiKey)
                .addHeader("X-RapidAPI-Host", "exercisedb.p.rapidapi.com")
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Gson().fromJson(responseBody, workoutItem::class.java)
            } else {
                Log.w("HTTPError", "HTTP Request not successful: ${response.code}")
                null
            }
        } catch (e: Exception) {
            Log.e("NetworkError", "Failed to fetch workout details", e)
            null
        }
    }

}