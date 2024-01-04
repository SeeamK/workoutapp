import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.workoutcollector.FilterItem
import com.example.workoutcollector.R
import com.example.workoutcollector.workoutList

class FilterAdapter(private val filterList: List<FilterItem>, private val context: Context) :
    RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    // Provide a reference to the views for each data item
    class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var filterIcon: ImageView = itemView.findViewById(R.id.filtericon)
        var filterName: TextView = itemView.findViewById(R.id.filtername)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.filtercard, parent, false)
        return FilterViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the contents of the view with that element
        val filterItem = filterList[position]
//        holder.filterIcon.setImageResource(filterItem.icon)
        holder.filterName.text = filterItem.name

        // Set the image resource based on the filter name
        when (filterItem.name) {
            context.getString(R.string.abductors) -> holder.filterIcon.setImageResource(R.drawable.abductors)
            context.getString(R.string.abs) -> holder.filterIcon.setImageResource(R.drawable.abs)
            context.getString(R.string.adductors) -> holder.filterIcon.setImageResource(R.drawable.adductors)
            context.getString(R.string.biceps) -> holder.filterIcon.setImageResource(R.drawable.biceps)
            context.getString(R.string.calves) -> holder.filterIcon.setImageResource(R.drawable.calves)
            context.getString(R.string.cardiovascular_system) -> holder.filterIcon.setImageResource(R.drawable.cardiovascular_system)
            context.getString(R.string.delts) -> holder.filterIcon.setImageResource(R.drawable.delts)
            context.getString(R.string.forearms) -> holder.filterIcon.setImageResource(R.drawable.forearms)
            context.getString(R.string.glutes) -> holder.filterIcon.setImageResource(R.drawable.glutes)
            context.getString(R.string.hamstrings) -> holder.filterIcon.setImageResource(R.drawable.hamstrings)
            context.getString(R.string.lats) -> holder.filterIcon.setImageResource(R.drawable.lats)
            context.getString(R.string.levator_scapulae) -> holder.filterIcon.setImageResource(R.drawable.levator_scapulae)
            context.getString(R.string.pectorals) -> holder.filterIcon.setImageResource(R.drawable.pectorals)
            context.getString(R.string.quads) -> holder.filterIcon.setImageResource(R.drawable.quads)
            context.getString(R.string.serratus_anterior) -> holder.filterIcon.setImageResource(R.drawable.serratus_anterior)
            context.getString(R.string.spine) -> holder.filterIcon.setImageResource(R.drawable.spine)
            context.getString(R.string.traps) -> holder.filterIcon.setImageResource(R.drawable.traps)
            context.getString(R.string.triceps) -> holder.filterIcon.setImageResource(R.drawable.triceps)
            context.getString(R.string.upper_back) -> holder.filterIcon.setImageResource(R.drawable.upper_back)
            //equipments
            context.getString(R.string.barbell) -> holder.filterIcon.setImageResource(R.drawable.barbell)
            context.getString(R.string.ez_barbell) -> holder.filterIcon.setImageResource(R.drawable.ezbar)
            context.getString(R.string.smith_machine) -> holder.filterIcon.setImageResource(R.drawable.smithmachine)
            context.getString(R.string.rope) -> holder.filterIcon.setImageResource(R.drawable.rope)
            context.getString(R.string.band) -> holder.filterIcon.setImageResource(R.drawable.band)
            context.getString(R.string.resistance_band) -> holder.filterIcon.setImageResource(R.drawable.band)
            context.getString(R.string.elliptical_machine) -> holder.filterIcon.setImageResource(R.drawable.elliptical)
            context.getString(R.string.skierg_machine) -> holder.filterIcon.setImageResource(R.drawable.skierg)
            context.getString(R.string.dumbbell) -> holder.filterIcon.setImageResource(R.drawable.dumbell)
            context.getString(R.string.bosu_ball) -> holder.filterIcon.setImageResource(R.drawable.bosuball)
            context.getString(R.string.body_weight) -> holder.filterIcon.setImageResource(R.drawable.bodyweight)
            context.getString(R.string.cable) -> holder.filterIcon.setImageResource(R.drawable.cablemachine)
            context.getString(R.string.kettle_bell) -> holder.filterIcon.setImageResource(R.drawable.kettlebell)
            context.getString(R.string.leverage_machine) -> holder.filterIcon.setImageResource(R.drawable.leveragemachine)
            context.getString(R.string.sled_machine) -> holder.filterIcon.setImageResource(R.drawable.sledmachine)
            context.getString(R.string.roller) -> holder.filterIcon.setImageResource(R.drawable.roller)
            context.getString(R.string.olympic_barbell) -> holder.filterIcon.setImageResource(R.drawable.barbell)
            context.getString(R.string.medicine_ball) -> holder.filterIcon.setImageResource(R.drawable.medicineball)
            context.getString(R.string.stability_ball) -> holder.filterIcon.setImageResource(R.drawable.medicineball)
            context.getString(R.string.stationary_bike) -> holder.filterIcon.setImageResource(R.drawable.bike)
            context.getString(R.string.stepmill_machine) -> holder.filterIcon.setImageResource(R.drawable.stepmill)
            context.getString(R.string.tire) -> holder.filterIcon.setImageResource(R.drawable.tire)
            context.getString(R.string.trap_bar) -> holder.filterIcon.setImageResource(R.drawable.trapbar)
            context.getString(R.string.weighted) -> holder.filterIcon.setImageResource(R.drawable.weight)
            context.getString(R.string.wheel_roller) -> holder.filterIcon.setImageResource(R.drawable.wheelroller)

            else -> holder.filterIcon.setImageResource(R.drawable.muscleman)
        }

        // Set the click listener
        holder.itemView.setOnClickListener {
            // Create an intent with the context passed to the adapter and the activity class that you want to start
            val intent = Intent(context, workoutList::class.java).apply {
                // Pass the filter name as an extra
                putExtra("Name", filterItem.name)
            }
            // Start the activity using the context
            context.startActivity(intent)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = filterList.size
}
