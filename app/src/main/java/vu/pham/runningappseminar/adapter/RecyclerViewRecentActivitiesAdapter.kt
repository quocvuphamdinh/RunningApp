package vu.pham.runningappseminar.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.model.UserActivity
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class RecyclerViewRecentActivitiesAdapter :RecyclerView.Adapter<RecyclerViewRecentActivitiesAdapter.RecentActivitiesHolder>(){
    private var recentActivitiesList:ArrayList<UserActivity> = ArrayList()

    fun setData(recentActivitiesList:ArrayList<UserActivity>){
        this.recentActivitiesList = recentActivitiesList
        notifyDataSetChanged()
    }

    class RecentActivitiesHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var txtNameActivity:TextView
        var txtDate:TextView
        var txtDistance:TextView
        var txtTimeInMillies:TextView
        var txtAvgSpeed:TextView
        var txtCaloriesBurned:TextView

        init {
            txtNameActivity = itemView.findViewById(R.id.textViewNameRecentActivitiesItem)
            txtDate = itemView.findViewById(R.id.textViewDateRecentActivitiesItem)
            txtDistance = itemView.findViewById(R.id.textViewDistanceRecentActivitiesItem)
            txtTimeInMillies = itemView.findViewById(R.id.textViewTimeInMillies)
            txtAvgSpeed = itemView.findViewById(R.id.textViewAvgSpeed)
            txtCaloriesBurned = itemView.findViewById(R.id.textViewCaloriesBurned)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentActivitiesHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recent_activities_item_row, parent, false)
        return RecentActivitiesHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecentActivitiesHolder, position: Int) {
        val userActivity = recentActivitiesList[position]
        holder.txtNameActivity.text = userActivity.getActivity().getName()
        val calendar = Calendar.getInstance()
        calendar.time = userActivity.getRun().getDayOfWeek()
        val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        holder.txtDate.text = "$dayOfWeek ${userActivity.getRun().getDayOfWeek().date}, " +
                "${userActivity.getRun().getRunTime().hours}:${userActivity.getRun().getRunTime().minutes}"
        holder.txtDistance.text = "${userActivity.getRun().getDistanceInKMH()} km"

//        val millies = userActivity.getRun().getTimeInMillies().time
        val minutes = userActivity.getRun().getTimeInMillies().minutes//((millies / 1000) / 60)
        val seconds = userActivity.getRun().getTimeInMillies().seconds//((millies / 1000) % 60)
        holder.txtTimeInMillies.text = "$minutes:$seconds"

        holder.txtAvgSpeed.text = "${userActivity.getRun().getAvgSpeed()} kph"
        holder.txtCaloriesBurned.text = "${userActivity.getRun().getCaloriesBurned()} Kcal"
    }

    override fun getItemCount(): Int {
        return 2
    }
}