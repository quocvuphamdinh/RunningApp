package vu.pham.runningappseminar.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.models.Activity
import vu.pham.runningappseminar.utils.TrackingUtil


class RecyclerViewActivityAdapter(
    val layout:Int,
    private val clickItem: ClickItem,
    private val isShowAll:Boolean,
    private val isRunning:Boolean,
    private val isHaveImageBackground:Boolean,
) : RecyclerView.Adapter<RecyclerViewActivityAdapter.ActivityHolder>(){

    interface ClickItem{
        fun clickItem(activity: Activity)
    }

    inner class ActivityHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var txtNameItem : TextView
        var txtTimeSumItem : TextView
        var linearLayout :LinearLayout
        var imageViewCompleted: ImageView

        init {
            txtNameItem = itemView.findViewById(R.id.textViewActivityItemName)
            txtTimeSumItem = itemView.findViewById(R.id.textViewActivityItemTimeSum)
            linearLayout = itemView.findViewById(R.id.linearItemExcersiseLayout)
            imageViewCompleted = itemView.findViewById(R.id.imageViewCompleted)
        }
    }

    val differCallBack = object : DiffUtil.ItemCallback<Activity>(){

        override fun areItemsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem.getId() == newItem.getId()
        }

        override fun areContentsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }


    val differ = AsyncListDiffer(this, differCallBack)

    fun submitList(list:List<Activity>) = differ.submitList(list)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityHolder {
       return ActivityHolder(LayoutInflater.from(parent.context).inflate(layout, parent, false))
    }

    override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
        val activity = differ.currentList[position]
        holder.txtNameItem.text = activity.getName()
        var duration = 0L
        for(i in 0 until activity.getWorkouts().size){
            duration+= activity.getWorkouts()[i].getDuration()
        }
        holder.txtTimeSumItem.text = "${TrackingUtil.getFormattedTimer2(duration, 2)} min"
        if(isHaveImageBackground){
            if(isRunning){
                holder.linearLayout.setBackgroundResource(R.drawable.activity_background)
            }else{
                holder.linearLayout.setBackgroundResource(R.drawable.walking_background)
            }
        }
        if(activity.getIsCompleted()==1){
            holder.imageViewCompleted.visibility = View.VISIBLE
        }else{
            holder.imageViewCompleted.visibility = View.INVISIBLE
        }

        holder.txtNameItem.setOnClickListener {
            clickItem.clickItem(activity)
        }
        holder.txtTimeSumItem.setOnClickListener {
            clickItem.clickItem(activity)
        }
        holder.linearLayout.setOnClickListener {
            clickItem.clickItem(activity)
        }
    }

    override fun getItemCount(): Int {
        if(isShowAll){
            return differ.currentList.size
        }else{
            if(differ.currentList.size > 4 ){
                return 4
            }
            return differ.currentList.size
        }
    }
}