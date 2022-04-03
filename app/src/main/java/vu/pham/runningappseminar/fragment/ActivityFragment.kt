package vu.pham.runningappseminar.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.activity.DetailExerciseActivity
import vu.pham.runningappseminar.adapter.RecyclerViewActivityAdapter
import vu.pham.runningappseminar.databinding.ActivityFragmentBinding
import vu.pham.runningappseminar.model.Activity
import vu.pham.runningappseminar.model.Data
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.MainViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.MainViewModelFactory

class ActivityFragment : Fragment() {
    private lateinit var adapterRunning:RecyclerViewActivityAdapter
    private lateinit var adapterWalking:RecyclerViewActivityAdapter
    private lateinit var binding:ActivityFragmentBinding
    private val viewModel : MainViewModel by viewModels{
        MainViewModelFactory((activity?.application as RunApplication).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getListActivityWalkingFromRemote()
        getListActivityRunningFromRemote()
    }

    private fun getListActivityWalkingFromRemote(){
        viewModel.getListActivityByType(0).enqueue(object : Callback<List<Activity>>{
            override fun onResponse(call: Call<List<Activity>>, response: Response<List<Activity>>) {
                initWalkingActivity(response.body()!!)
            }

            override fun onFailure(call: Call<List<Activity>>, t: Throwable) {
                Toast.makeText(context, "Error: $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getListActivityRunningFromRemote(){
        viewModel.getListActivityByType(1).enqueue(object : Callback<List<Activity>>{
            override fun onResponse(call: Call<List<Activity>>, response: Response<List<Activity>>) {
                initRunningActivity(response.body()!!)
            }

            override fun onFailure(call: Call<List<Activity>>, t: Throwable) {
                Toast.makeText(context, "Error: $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun goToActivityDetailPage(id:Long){
        val intent = Intent(context, DetailExerciseActivity::class.java)
        val bundle = Bundle()
        bundle.putLong(Constants.DETAIL_EXERCISE_ID, id)
        intent.putExtras(bundle)
        startActivity(intent)
    }
    private fun initWalkingActivity(list: List<Activity>) {
        adapterWalking = RecyclerViewActivityAdapter(R.layout.walking_item_row, object : RecyclerViewActivityAdapter.ClickItem{
            override fun clickItem(activity: Activity) {
                goToActivityDetailPage(activity.getId())
            }
        })
        adapterWalking.submitList(list)
        binding.recyclerViewWalkingActivity.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewWalkingActivity.adapter = adapterWalking
        binding.recyclerViewWalkingActivity.setHasFixedSize(true)
        binding.recyclerViewWalkingActivity.isNestedScrollingEnabled = false
    }

    private fun initRunningActivity(list: List<Activity>) {
        adapterRunning = RecyclerViewActivityAdapter(R.layout.running_item_row, object : RecyclerViewActivityAdapter.ClickItem{
            override fun clickItem(activity: Activity) {
                goToActivityDetailPage(activity.getId())
            }
        })
        adapterRunning.submitList(list)
        binding.recyclerViewRunningActivity.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewRunningActivity.adapter = adapterRunning
        binding.recyclerViewRunningActivity.setHasFixedSize(true)
        binding.recyclerViewRunningActivity.isNestedScrollingEnabled = false
    }
}