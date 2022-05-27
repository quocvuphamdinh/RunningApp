package vu.pham.runningappseminar.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.ui.adapters.RecyclerViewActivityAdapter
import vu.pham.runningappseminar.databinding.FragmentActivityBinding
import vu.pham.runningappseminar.models.Activity
import vu.pham.runningappseminar.utils.CheckConnection
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.ExerciseViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.ExerciseViewModelFactory

class ActivityFragment : Fragment() {
    private lateinit var adapterRunning:RecyclerViewActivityAdapter
    private lateinit var adapterWalking:RecyclerViewActivityAdapter
    private lateinit var binding:FragmentActivityBinding
    private val viewModel : ExerciseViewModel by viewModels{
        ExerciseViewModelFactory((activity?.application as RunApplication).repository,  activity?.application as RunApplication)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(CheckConnection.haveNetworkConnection(requireContext())){
            setUpRecyclerviewWalkingActivity()
            setUpRecyclerviewRunningActivity()
            getListActivityWalkingFromRemote()
            getListActivityRunningFromRemote()
        }
        binding.textViewMoreWalking.setOnClickListener {
            if(CheckConnection.haveNetworkConnection(requireContext())){
                goToListExercisePage("Walking for weight loss", 0)
            }else{
                Toast.makeText(context, "Your device does not have internet !", Toast.LENGTH_LONG).show()
            }
        }

        binding.textViewMoreRunning.setOnClickListener {
            if(CheckConnection.haveNetworkConnection(requireContext())){
                goToListExercisePage("Running for weight loss", 1)
            }else{
                Toast.makeText(context, "Your device does not have internet !", Toast.LENGTH_LONG).show()
            }
        }
        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
        }
    }

    private fun goToListExercisePage(titleName:String, typeExercise:Int){
        viewModel.clearToast()
        val bundle = Bundle()
        bundle.putString(Constants.TITLE_NAME, titleName)
        bundle.putInt(Constants.TYPE_EXERCISE, typeExercise)
        findNavController().navigate(R.id.action_activityFragment_to_listExerciseFragment, bundle)
    }

    private fun getListActivityWalkingFromRemote(){
        viewModel.getListActivityWalk()
        viewModel.listActivityWalk.observe(viewLifecycleOwner, Observer {
            adapterWalking.submitList(it)
        })
    }

    private fun getListActivityRunningFromRemote(){
        viewModel.getListActivityRun()
        viewModel.listActivityRun.observe(viewLifecycleOwner, Observer {
            adapterRunning.submitList(it)
        })
    }

    private fun goToActivityDetailPage(id:Long){
        viewModel.clearToast()
        val bundle = Bundle()
        bundle.putLong(Constants.DETAIL_EXERCISE_ID, id)
        findNavController().navigate(R.id.action_activityFragment_to_detailExerciseFragment, bundle)
    }

    private fun setUpRecyclerviewWalkingActivity() {
        adapterWalking = RecyclerViewActivityAdapter(R.layout.walking_item_row, object : RecyclerViewActivityAdapter.ClickItem{
            override fun clickItem(activity: Activity) {
                if(CheckConnection.haveNetworkConnection(requireContext())){
                    goToActivityDetailPage(activity.getId())
                }else{
                    Toast.makeText(context, "Your device does not have internet !", Toast.LENGTH_LONG).show()
                }
            }
        }, false, false, false)
        binding.recyclerViewWalkingActivity.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewWalkingActivity.adapter = adapterWalking
        binding.recyclerViewWalkingActivity.isNestedScrollingEnabled = false
    }

    private fun setUpRecyclerviewRunningActivity() {
        adapterRunning = RecyclerViewActivityAdapter(R.layout.running_item_row, object : RecyclerViewActivityAdapter.ClickItem{
            override fun clickItem(activity: Activity) {
                if(CheckConnection.haveNetworkConnection(requireContext())){
                    goToActivityDetailPage(activity.getId())
                }else{
                    Toast.makeText(context, "Your device does not have internet !", Toast.LENGTH_LONG).show()
                }
            }
        }, false, true, false)
        binding.recyclerViewRunningActivity.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewRunningActivity.adapter = adapterRunning
        binding.recyclerViewRunningActivity.isNestedScrollingEnabled = false
    }

    override fun onPause() {
        super.onPause()
        viewModel.clearToast()
    }
}