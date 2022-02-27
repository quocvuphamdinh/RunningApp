package vu.pham.runningappseminar.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.activity.HomeActivity
import vu.pham.runningappseminar.activity.SetMyGoalActivity
import vu.pham.runningappseminar.adapter.RecyclerViewActivityAdapter
import vu.pham.runningappseminar.adapter.RecyclerViewRecentActivitiesAdapter
import vu.pham.runningappseminar.model.Data

class HomeFragment : Fragment() {
    private lateinit var recyclerViewTodayTraining:RecyclerView
    private lateinit var recyclerViewRecentActivities:RecyclerView
    private lateinit var adapterTodayTraining:RecyclerViewActivityAdapter
    private lateinit var adapterRecentActivities:RecyclerViewRecentActivitiesAdapter
    private lateinit var imgSetMyGoal:ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home_fragment, container, false)

        anhXa(view)
        initActivityList()
        initRecentActivities()
        clickGoToSetMyGoal()
        return view
    }

    private fun clickGoToSetMyGoal() {
        imgSetMyGoal.setOnClickListener {
            val intent = Intent(context, SetMyGoalActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initRecentActivities() {
        adapterRecentActivities = RecyclerViewRecentActivitiesAdapter()
        adapterRecentActivities.setData(Data.userActivityList)
        recyclerViewRecentActivities.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewRecentActivities.adapter = adapterRecentActivities
        recyclerViewRecentActivities.setHasFixedSize(true)
        recyclerViewRecentActivities.isNestedScrollingEnabled = false
    }

    private fun initActivityList() {
        adapterTodayTraining = RecyclerViewActivityAdapter()
        adapterTodayTraining.setData(Data.ActivityList, R.layout.activity_item_row)
        recyclerViewTodayTraining.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewTodayTraining.adapter = adapterTodayTraining
        recyclerViewTodayTraining.setHasFixedSize(true)
    }

    private fun anhXa(view: View) {
        recyclerViewTodayTraining = view.findViewById(R.id.recyclerViewActivityHomeFragment)
        recyclerViewRecentActivities = view.findViewById(R.id.recyclerViewRecentActiviesHomeFragment)
        imgSetMyGoal = view.findViewById(R.id.imageViewSetMyGoal)
    }
}