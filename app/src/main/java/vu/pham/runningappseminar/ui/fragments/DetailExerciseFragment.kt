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
import vu.pham.runningappseminar.ui.adapters.RecyclerViewWorkoutAdapter
import vu.pham.runningappseminar.databinding.FragmentDetailExerciseBinding
import vu.pham.runningappseminar.models.Activity
import vu.pham.runningappseminar.models.Workout
import vu.pham.runningappseminar.utils.CheckConnection
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.DetailExerciseViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.DetailExerciseViewModelFactory

class DetailExerciseFragment : Fragment() {
    private lateinit var binding : FragmentDetailExerciseBinding
    private lateinit var workoutAdapter: RecyclerViewWorkoutAdapter
    private var id:Long?=null
    private var workouts = ArrayList<Workout>()
    private val viewModel : DetailExerciseViewModel by viewModels{
        DetailExerciseViewModelFactory((activity?.application as RunApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getIdFromPreviousPage()
        getActivityDetailFromRemote()

        binding.imageCloseDetailExcerciseActivity.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.imageStartDetailExcerciseActivity.setOnClickListener {
            if(CheckConnection.haveNetworkConnection(requireContext())){
                val bundle = Bundle()
                bundle.putParcelableArrayList(Constants.DURATION_EXERCISE, workouts)
                bundle.putLong(Constants.ID_EXERCISE, id!!)
                findNavController().navigate(R.id.action_detailExerciseFragment_to_exerciseRunFragment, bundle)
            }else{
                Toast.makeText(context, "Your device does not have internet !", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getIdFromPreviousPage(){
        val bundle = arguments
        id = bundle?.getLong(Constants.DETAIL_EXERCISE_ID)
    }
    private fun getActivityDetailFromRemote(){
        id?.let {
            viewModel.getActivityDetail(id!!)
            viewModel.activityDetail.observe(viewLifecycleOwner, Observer {
                setUpRecyclerView(it.getWorkouts())
                bindDataToView(it)
                workouts = it.getWorkouts() as ArrayList<Workout>
            })
        }
    }

    private fun bindDataToView(activity: Activity){
        binding.textViewTitleDetailExercise.text = activity.getName()
        setUpRecyclerView(activity.getWorkouts())
    }
    private fun setUpRecyclerView(list: List<Workout>) {
        workoutAdapter = RecyclerViewWorkoutAdapter()
        workoutAdapter.submitList(list)
        binding.rcvDetailExercise.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvDetailExercise.adapter = workoutAdapter
        binding.rcvDetailExercise.setHasFixedSize(true)
    }
}