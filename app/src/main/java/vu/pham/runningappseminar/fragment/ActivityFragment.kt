package vu.pham.runningappseminar.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.adapter.RecyclerViewActivityAdapter
import vu.pham.runningappseminar.model.Data

class ActivityFragment : Fragment() {
    private lateinit var recyclerViewWalkingActivity:RecyclerView
    private lateinit var recyclerViewRunningActivity:RecyclerView
    private lateinit var adapterRunning:RecyclerViewActivityAdapter
    private lateinit var adapterWalking:RecyclerViewActivityAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_fragment, container, false)

        anhXa(view)
        initWalkingActivity()
        initRunningActivity()
        return view
    }

    private fun initWalkingActivity() {
        adapterWalking = RecyclerViewActivityAdapter()
        adapterWalking.setData(Data.ActivityList, R.layout.walking_item_row)
        recyclerViewWalkingActivity.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewWalkingActivity.adapter = adapterWalking
        recyclerViewWalkingActivity.setHasFixedSize(true)
        recyclerViewWalkingActivity.isNestedScrollingEnabled = false
    }

    private fun initRunningActivity() {
        adapterRunning = RecyclerViewActivityAdapter()
        adapterRunning.setData(Data.ActivityList, R.layout.running_item_row)
        recyclerViewRunningActivity.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewRunningActivity.adapter = adapterRunning
        recyclerViewRunningActivity.setHasFixedSize(true)
        recyclerViewRunningActivity.isNestedScrollingEnabled = false
    }

    private fun anhXa(view: View) {
        recyclerViewWalkingActivity= view.findViewById(R.id.recyclerViewWalkingActivity)
        recyclerViewRunningActivity = view.findViewById(R.id.recyclerViewRunningActivity)
    }
}