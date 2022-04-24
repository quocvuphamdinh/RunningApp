package vu.pham.runningappseminar.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.adapters.RecyclerViewActivityAdapter
import vu.pham.runningappseminar.databinding.FragmentListExerciseBinding
import vu.pham.runningappseminar.models.Activity
import vu.pham.runningappseminar.utils.CheckConnection
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.ListExerciseViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.ListExerciseViewModelFactory

class ListExerciseFragment : Fragment() {
    private lateinit var binding : FragmentListExerciseBinding
    private lateinit var exerciseAdapter : RecyclerViewActivityAdapter
    private var titleName=""
    private var type=-1
    private val viewModel : ListExerciseViewModel by viewModels{
        ListExerciseViewModelFactory((activity?.application as RunApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()
        getListExercise()
        registerObserver()

        binding.imageViewBackListExercise.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getData() {
        val bundle = arguments
        bundle?.let {
            titleName = it.getString(Constants.TITLE_NAME)!!
            type = it.getInt(Constants.TYPE_EXERCISE)
            binding.textViewTitle.text = titleName
        }
    }

    private fun getListExercise(){
        if(type==0){
            viewModel.getWalkingExercises()
        }else{
            viewModel.getRunningExercises()
        }
    }
    private fun registerObserver() {
        if(type==0){
            viewModel.walkingExercises.observe(viewLifecycleOwner, Observer {
                setUpRecyclerViewRunning(it, false, R.layout.walking_item_row)
            })
        }else{
            viewModel.runningExercises.observe(viewLifecycleOwner, Observer {
                setUpRecyclerViewRunning(it, true, R.layout.running_item_row)
            })
        }
    }

    private fun goToActivityDetailPage(id:Long){
        val bundle = Bundle()
        bundle.putLong(Constants.DETAIL_EXERCISE_ID, id)
        findNavController().navigate(R.id.action_listExerciseFragment_to_detailExerciseFragment, bundle)
    }

    private fun setUpRecyclerViewRunning(list:List<Activity>, isRunning:Boolean, layout:Int) {
        exerciseAdapter = RecyclerViewActivityAdapter(layout, object : RecyclerViewActivityAdapter.ClickItem{
            override fun clickItem(activity: Activity) {
                if(CheckConnection.haveNetworkConnection(requireContext())){
                    goToActivityDetailPage(activity.getId())
                }else{
                    Toast.makeText(context, "Your device does not have internet !", Toast.LENGTH_LONG).show()
                }
            }
        }, true, isRunning, false)
        exerciseAdapter.submitList(list)
        binding.rcvExerciseWeightLoss.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvExerciseWeightLoss.adapter = exerciseAdapter
        binding.rcvExerciseWeightLoss.setHasFixedSize(true)
    }
}