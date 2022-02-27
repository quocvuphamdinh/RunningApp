package vu.pham.runningappseminar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.model.Activity

class RecyclerViewActivityAdapter : RecyclerView.Adapter<RecyclerViewActivityAdapter.ActivityHolder>(){
    private var activityList:ArrayList<Activity> = ArrayList()
    private var layout:Int=0

    fun setData(activityList:ArrayList<Activity>, layout:Int){
        this.activityList = activityList
        this.layout = layout
        notifyDataSetChanged()
    }

    class ActivityHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var txtNameItem : TextView
        var txtTimeSumItem : TextView

        init {
            txtNameItem = itemView.findViewById(R.id.textViewActivityItemName)
            txtTimeSumItem = itemView.findViewById(R.id.textViewActivityItemTimeSum)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityHolder {
       val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ActivityHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
        val activity = activityList[position]
        holder.txtNameItem.text = activity.getName()
        holder.txtTimeSumItem.text = "${activity.getDurationOfWorkouts()} min"
    }

    override fun getItemCount(): Int {
        return 4
    }
}