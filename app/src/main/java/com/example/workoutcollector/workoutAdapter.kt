import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.workoutcollector.R
import com.squareup.picasso.Picasso
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.example.workoutcollector.workoutFilterPage
import com.example.workoutcollector.workoutInfo
import com.example.workoutcollector.workoutItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import javax.sql.DataSource

class WorkoutAdapter(
    private val context: Context,
    private val workoutList: MutableList<workoutItem>,
    private val name: String
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView) {
        val workoutName: TextView = cardView.findViewById(R.id.workout_name)
        val target: TextView = cardView.findViewById(R.id.target)
        val equipment: TextView = cardView.findViewById(R.id.equipment)
        val gif: ImageView = cardView.findViewById(R.id.gif)
        val progressBar: ProgressBar = cardView.findViewById(R.id.gifProgressBar)
        val rmBtn: FloatingActionButton = cardView.findViewById(R.id.rmWorkout)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.workoutcard, parent, false) as CardView
        return WorkoutViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val accountChoices = listOf<String>("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday", "favorites")
        val workoutItem = workoutList[position]
        holder.workoutName.text = workoutItem.name
        holder.target.text = workoutItem.target
        holder.equipment.text = workoutItem.equipment
        holder.rmBtn.visibility = View.GONE

        if(accountChoices.contains(name.lowercase())){
            holder.rmBtn.visibility = View.VISIBLE
            holder.rmBtn.setOnClickListener {
                val fireBaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
                val user = fireBaseAuth.currentUser
                if (user != null) {
                    val database = Firebase.database
                    val ref = database.getReference("Users/${user.uid}/${name}")

                    // Query the nodes to find the one with the value equal to workoutItem.id
                    ref.orderByValue().equalTo(workoutItem.id).addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            // Iterate over the children of the dataSnapshot
                            for (childSnapshot in dataSnapshot.children) {
                                // Remove the node that contains the value of workoutItem.id
                                childSnapshot.ref.removeValue().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d("Firebase", "Node with value ${workoutItem.id} deleted successfully.")
                                        // Notify that item is removed, if you want to update the UI immediately
                                        notifyItemRemoved(position)
                                        // remove the item from the list
                                        workoutList.removeAt(position)
                                    } else {
                                        Log.e("Firebase", "Failed to delete node.", task.exception)
                                    }
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.e("Firebase", "Database error: $databaseError")
                        }
                    })
                } else {
                    Log.e("FirebaseAuth", "User is not logged in.")
                }
            }
        }

        // Show the progress bar while loading
        holder.progressBar.visibility = View.VISIBLE

        if(workoutItem.gifUrl.isNotEmpty()){
            Glide.with(context)
                .asGif()
                .load(workoutItem.gifUrl)
                .listener(object : RequestListener<GifDrawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<GifDrawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        // Hide the progress bar on failure
                        holder.progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: GifDrawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<GifDrawable>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        // Hide the progress bar when the resource is ready
                        holder.progressBar.visibility = View.GONE
                        return false
                    }
                })
                .into(holder.gif)
        } else {
            // Hide the progress bar if there is no URL
            holder.progressBar.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            // Create an intent with the context passed to the adapter and the activity class that you want to start
            val intent = Intent(context, workoutInfo::class.java).apply {
                // Pass the filter name as an extra
                putExtra("id", workoutItem.id)
            }
            // Start the activity using the context
            context.startActivity(intent)
        }

    }

    override fun getItemCount() = workoutList.size
}
