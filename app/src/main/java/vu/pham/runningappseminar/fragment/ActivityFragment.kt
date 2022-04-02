package vu.pham.runningappseminar.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.adapter.RecyclerViewActivityAdapter
import vu.pham.runningappseminar.databinding.ActivityFragmentBinding
import vu.pham.runningappseminar.model.Data

class ActivityFragment : Fragment() {
    private lateinit var adapterRunning:RecyclerViewActivityAdapter
    private lateinit var adapterWalking:RecyclerViewActivityAdapter
    private lateinit var binding:ActivityFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWalkingActivity()
        initRunningActivity()
    }

    private fun initWalkingActivity() {
        adapterWalking = RecyclerViewActivityAdapter()
        adapterWalking.setData(Data.ActivityList, R.layout.walking_item_row)
        binding.recyclerViewWalkingActivity.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewWalkingActivity.adapter = adapterWalking
        binding.recyclerViewWalkingActivity.setHasFixedSize(true)
        binding.recyclerViewWalkingActivity.isNestedScrollingEnabled = false
    }

    private fun initRunningActivity() {
        adapterRunning = RecyclerViewActivityAdapter()
        adapterRunning.setData(Data.ActivityList, R.layout.running_item_row)
        binding.recyclerViewRunningActivity.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewRunningActivity.adapter = adapterRunning
        binding.recyclerViewRunningActivity.setHasFixedSize(true)
        binding.recyclerViewRunningActivity.isNestedScrollingEnabled = false
    }
}