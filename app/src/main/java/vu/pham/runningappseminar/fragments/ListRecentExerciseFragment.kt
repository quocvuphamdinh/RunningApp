package vu.pham.runningappseminar.fragments

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
import vu.pham.runningappseminar.adapters.RecyclerViewRecentActivitiesAdapter
import vu.pham.runningappseminar.databinding.FragmentListRecentExerciseBinding
import vu.pham.runningappseminar.models.UserActivityDetail
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.utils.TrackingUtil
import vu.pham.runningappseminar.viewmodels.ListRecentExerciseViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.ListRecentExerciseViewModelFactory

class ListRecentExerciseFragment : Fragment() {
    private lateinit var binding : FragmentListRecentExerciseBinding
    private lateinit var adapterRecentActivities : RecyclerViewRecentActivitiesAdapter
    private val viewModel : ListRecentExerciseViewModel by viewModels{
        ListRecentExerciseViewModelFactory((activity?.application as RunApplication).repository,
            activity?.application as RunApplication
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListRecentExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()
        subcribeToObservers()
        binding.imageCloseRecent.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun subcribeToObservers() {
        viewModel.recentExercises.observe(viewLifecycleOwner, Observer {
            setUpRecentActivities(it)
        })

        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            binding.textViewTotalDistanceRecent.text = (it/1000f).toString()
        })

        viewModel.totalDuration.observe(viewLifecycleOwner, Observer {
            binding.textViewTotalDurationRecent.text = TrackingUtil.getFormattedTimer3(it)
        })

        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer {
            binding.textViewTotalCaloriesBurnedRecent.text = it.toString()
        })

        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
            binding.textViewTotalAvgSpeedRecent.text = it.toString()
        })

        viewModel.errEvent.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()){
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getData(){
        val bundle = arguments
        bundle?.let {
            val id = bundle.getLong(Constants.ID_USER_RECENT_EXERCISE)
            viewModel.getListRecentExercises(id)
            viewModel.calculateDataRecentExercise(id)
        }
    }

    private fun setUpRecentActivities(list : List<UserActivityDetail>) {
        adapterRecentActivities = RecyclerViewRecentActivitiesAdapter(true)
        adapterRecentActivities.submitList(list)
        binding.rcvMoreRecentExercise.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rcvMoreRecentExercise.adapter = adapterRecentActivities
        binding.rcvMoreRecentExercise.setHasFixedSize(true)
        binding.rcvMoreRecentExercise.isNestedScrollingEnabled = false
    }
}