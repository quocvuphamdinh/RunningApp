package vu.pham.runningappseminar.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.adapters.RecyclerViewActivityAdapter
import vu.pham.runningappseminar.databinding.FragmentActivityBinding
import vu.pham.runningappseminar.models.Activity
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.MainViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.MainViewModelFactory

class ActivityFragment : Fragment() {
    private lateinit var adapterRunning:RecyclerViewActivityAdapter
    private lateinit var adapterWalking:RecyclerViewActivityAdapter
    private lateinit var binding:FragmentActivityBinding
    private val viewModel : MainViewModel by viewModels{
        MainViewModelFactory((activity?.application as RunApplication).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getListActivityWalkingFromRemote()
        getListActivityRunningFromRemote()

        binding.textViewMoreWalking.setOnClickListener {
            goToListExercisePage("Walking for weight loss", 0)
        }

        binding.textViewMoreRunning.setOnClickListener {
            goToListExercisePage("Running for weight loss", 1)
        }
    }

    private fun goToListExercisePage(titleName:String, typeExercise:Int){
        val bundle = Bundle()
        bundle.putString(Constants.TITLE_NAME, titleName)
        bundle.putInt(Constants.TYPE_EXERCISE, typeExercise)
        findNavController().navigate(R.id.action_activityFragment_to_listExerciseFragment, bundle)
    }

    private fun getListActivityWalkingFromRemote(){
        viewModel.getListActivityWalk()
        viewModel.listActivityWalk.observe(viewLifecycleOwner, Observer {
            initWalkingActivity(it)
        })
    }

    private fun getListActivityRunningFromRemote(){
        viewModel.getListActivityRun()
        viewModel.listActivityRun.observe(viewLifecycleOwner, Observer {
            initRunningActivity(it)
        })
    }

    private fun goToActivityDetailPage(id:Long){
        val bundle = Bundle()
        bundle.putLong(Constants.DETAIL_EXERCISE_ID, id)
        findNavController().navigate(R.id.action_activityFragment_to_detailExerciseFragment, bundle)
    }
    private fun initWalkingActivity(list:List<Activity>) {
        adapterWalking = RecyclerViewActivityAdapter(R.layout.walking_item_row, object : RecyclerViewActivityAdapter.ClickItem{
            override fun clickItem(activity: Activity) {
                goToActivityDetailPage(activity.getId())
            }
        }, false, false, false)
        adapterWalking.submitList(list)
        binding.recyclerViewWalkingActivity.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewWalkingActivity.adapter = adapterWalking
        binding.recyclerViewWalkingActivity.setHasFixedSize(true)
        binding.recyclerViewWalkingActivity.isNestedScrollingEnabled = false
    }

    private fun initRunningActivity(list:List<Activity>) {
        adapterRunning = RecyclerViewActivityAdapter(R.layout.running_item_row, object : RecyclerViewActivityAdapter.ClickItem{
            override fun clickItem(activity: Activity) {
                goToActivityDetailPage(activity.getId())
            }
        }, false, true, false)
        adapterRunning.submitList(list)
        binding.recyclerViewRunningActivity.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewRunningActivity.adapter = adapterRunning
        binding.recyclerViewRunningActivity.setHasFixedSize(true)
        binding.recyclerViewRunningActivity.isNestedScrollingEnabled = false
    }
}