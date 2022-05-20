package vu.pham.runningappseminar.ui.fragments

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
import vu.pham.runningappseminar.ui.adapters.RecyclerViewActivityAdapter
import vu.pham.runningappseminar.models.Activity
import vu.pham.runningappseminar.utils.CheckConnection
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.ListExerciseViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.ListExerciseViewModelFactory
import vu.pham.runningappseminar.databinding.FragmentListExerciseBinding


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
        getListExerciseAndSetUpRecyclerview()
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

    private fun getListExerciseAndSetUpRecyclerview(){
        if(type==0){
            setUpRecyclerView(false, R.layout.walking_item_row)
            viewModel.getWalkingExercises()
        }else{
            setUpRecyclerView(true, R.layout.running_item_row)
            viewModel.getRunningExercises()
        }
    }
    private fun registerObserver() {
        if(type==0){
            viewModel.walkingExercises.observe(viewLifecycleOwner, Observer {
                exerciseAdapter.submitList(it)
                binding.textViewNumber.text = "${it.count {item-> item.getIsCompleted()==1}}/${it.size}"
            })
        }else{
            viewModel.runningExercises.observe(viewLifecycleOwner, Observer {
                exerciseAdapter.submitList(it)
                binding.textViewNumber.text = "${it.count {item-> item.getIsCompleted()==1}}/${it.size}"
            })
        }
    }

    private fun goToActivityDetailPage(id:Long){
        val bundle = Bundle()
        bundle.putLong(Constants.DETAIL_EXERCISE_ID, id)
        findNavController().navigate(R.id.action_listExerciseFragment_to_detailExerciseFragment, bundle)
    }

    private fun setUpRecyclerView(isRunning:Boolean, layout:Int) {
        exerciseAdapter = RecyclerViewActivityAdapter(layout, object : RecyclerViewActivityAdapter.ClickItem{
            override fun clickItem(activity: Activity) {
                if(CheckConnection.haveNetworkConnection(requireContext())){
                    goToActivityDetailPage(activity.getId())
                }else{
                    Toast.makeText(context, "Your device does not have internet !", Toast.LENGTH_LONG).show()
                }
            }
        }, true, isRunning, false)
        binding.rcvExerciseWeightLoss.adapter = exerciseAdapter
        binding.rcvExerciseWeightLoss.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }
}