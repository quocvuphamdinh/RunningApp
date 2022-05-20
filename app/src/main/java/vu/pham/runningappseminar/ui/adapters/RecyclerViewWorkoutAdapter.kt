package vu.pham.runningappseminar.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.models.Workout
import vu.pham.runningappseminar.utils.TrackingUtil

class RecyclerViewWorkoutAdapter : RecyclerView.Adapter<RecyclerViewWorkoutAdapter.WorkoutViewHolder>() {

    var differCallback = object : DiffUtil.ItemCallback<Workout>(){
        override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean {
            return oldItem.getId() == newItem.getId()
        }

        override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
           return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    fun submitList(list:List<Workout>) = differ.submitList(list)

    inner class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var imageView:ImageView
        var txtName:TextView
        var txtDuration:TextView

        init {
            imageView = itemView.findViewById(R.id.imageViewWorkout)
            txtName = itemView.findViewById(R.id.textViewNameWorkout)
            txtDuration = itemView.findViewById(R.id.textViewDurationWorkout)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        return WorkoutViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_workout_row, parent, false))
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
       val workout = differ.currentList[position]
        if(workout.getName() == "Walk") holder.imageView.setImageResource(R.drawable.ic_walk) else if(workout.getName() == "Run") holder.imageView.setImageResource(R.drawable.ic_run_2) else holder.imageView.setImageResource(R.drawable.ic_cool_down)
        holder.txtName.text = workout.getName()
        holder.txtDuration.text = TrackingUtil.getFormattedTimer(workout.getDuration())
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}