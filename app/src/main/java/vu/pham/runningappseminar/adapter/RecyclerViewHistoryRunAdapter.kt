package vu.pham.runningappseminar.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.database.Run
import vu.pham.runningappseminar.utils.TrackingUtil
import java.text.SimpleDateFormat
import java.util.*

class RecyclerViewHistoryRunAdapter : RecyclerView.Adapter<RecyclerViewHistoryRunAdapter.RunViewHolder>() {


    inner class RunViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var textViewTimeStampHistoryRun:TextView
        var textViewAverageSpeedInKMHHistoryRun:TextView
        var textViewDistanceHistoryRun:TextView
        var textViewTimeInMillisHistoryRun:TextView
        var textViewCaloriesBurnedHistoryRun:TextView
        var textViewRunAtTimeHistoryRun:TextView

        init {
            textViewTimeStampHistoryRun = itemView.findViewById(R.id.textViewTimeStampHistoryRun)
            textViewAverageSpeedInKMHHistoryRun = itemView.findViewById(R.id.textViewAverageSpeedInKMHHistoryRun)
            textViewDistanceHistoryRun = itemView.findViewById(R.id.textViewDistanceHistoryRun)
            textViewTimeInMillisHistoryRun = itemView.findViewById(R.id.textViewTimeInMillisHistoryRun)
            textViewCaloriesBurnedHistoryRun = itemView.findViewById(R.id.textViewCaloriesBurnedHistoryRun)
            textViewRunAtTimeHistoryRun = itemView.findViewById(R.id.textViewTimeRunHistoryRun)
        }
    }

    val differCallBack = object : DiffUtil.ItemCallback<Run>(){
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    fun submitList(list:List<Run>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        return RunViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.history_run_item, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = run.timestamp
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.textViewTimeStampHistoryRun.text =dateFormat.format(calendar.time)
        holder.textViewAverageSpeedInKMHHistoryRun.text = "${run.averageSpeedInKilometersPerHour} km/h"
        holder.textViewDistanceHistoryRun.text ="${run.distanceInKilometers / 1000f} km"
        holder.textViewTimeInMillisHistoryRun.text = TrackingUtil.getFormattedTimer(run.timeInMillis)
        holder.textViewCaloriesBurnedHistoryRun.text = "${run.caloriesBurned} kcal"
        val dateFormat2 = SimpleDateFormat("hh:mm:ss", Locale.getDefault())
        holder.textViewRunAtTimeHistoryRun.text = dateFormat2.format(calendar.time)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}