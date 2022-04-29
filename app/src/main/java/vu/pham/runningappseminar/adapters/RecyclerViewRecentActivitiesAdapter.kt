package vu.pham.runningappseminar.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.models.UserActivityDetail
import vu.pham.runningappseminar.utils.TrackingUtil
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class RecyclerViewRecentActivitiesAdapter(val isShowAll : Boolean) :RecyclerView.Adapter<RecyclerViewRecentActivitiesAdapter.RecentActivitiesHolder>(){

    val differCallBack = object : DiffUtil.ItemCallback<UserActivityDetail>(){
        override fun areItemsTheSame(oldItem: UserActivityDetail, newItem: UserActivityDetail): Boolean {
            return oldItem.getId() == newItem.getId()
        }

        override fun areContentsTheSame(oldItem: UserActivityDetail, newItem: UserActivityDetail): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    fun submitList(list:List<UserActivityDetail>) = differ.submitList(list)


    inner class RecentActivitiesHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var txtNameActivity:TextView
        var txtDate:TextView
        var txtDistance:TextView
        var txtTimeInMillies:TextView
        var txtAvgSpeed:TextView
        var txtCaloriesBurned:TextView
        var img:ImageView

        init {
            txtNameActivity = itemView.findViewById(R.id.textViewNameRecentActivitiesItem)
            txtDate = itemView.findViewById(R.id.textViewDateRecentActivitiesItem)
            txtDistance = itemView.findViewById(R.id.textViewDistanceRecentActivitiesItem)
            txtTimeInMillies = itemView.findViewById(R.id.textViewTimeInMillies)
            txtAvgSpeed = itemView.findViewById(R.id.textViewAvgSpeed)
            txtCaloriesBurned = itemView.findViewById(R.id.textViewCaloriesBurned)
            img = itemView.findViewById(R.id.imageViewRecentActivityItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentActivitiesHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recent_activities_item_row, parent, false)
        return RecentActivitiesHolder(view)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: RecentActivitiesHolder, position: Int) {
        val userActivity = differ.currentList[position]
        if(userActivity.getActivity()?.getType()==0) {
            holder.img.setImageResource(R.drawable.walking_background)
        }else{
            holder.img.setImageResource(R.drawable.activity_background)
        }
        holder.txtNameActivity.text = userActivity.getActivity()?.getName()
        val calendar = Calendar.getInstance()
        calendar.time = Date(userActivity.getRun()?.timestamp!!)
        val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        val date = Date(userActivity.getRun()!!.timestamp)
        val format = SimpleDateFormat("HH:mm")
        holder.txtDate.text = "$dayOfWeek ${Date(userActivity.getRun()!!.timestamp).date}, " +
                format.format(date)
        holder.txtDistance.text = "${(userActivity.getRun()!!.distanceInKilometers)/1000f} km"

        holder.txtTimeInMillies.text = TrackingUtil.getFormattedTimer3(userActivity.getRun()!!.timeInMillis)

        holder.txtAvgSpeed.text = "${userActivity.getRun()!!.averageSpeedInKilometersPerHour} kph"
        holder.txtCaloriesBurned.text = "${userActivity.getRun()!!.caloriesBurned} Kcal"
    }

    override fun getItemCount(): Int {
        if(!isShowAll){
            if(differ.currentList.size > 2 ){
                return 2
            }
            return differ.currentList.size
        }
        return differ.currentList.size
    }
}