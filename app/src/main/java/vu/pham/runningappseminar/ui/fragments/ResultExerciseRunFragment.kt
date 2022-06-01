package vu.pham.runningappseminar.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.databinding.FragmentResultExerciseRunBinding
import vu.pham.runningappseminar.models.UserActivity
import vu.pham.runningappseminar.models.UserActivityDetail
import vu.pham.runningappseminar.ui.utils.LoadingDialog
import vu.pham.runningappseminar.utils.*
import vu.pham.runningappseminar.viewmodels.ResultExerciseRunViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.ResultExerciseRunViewModelFactory

class ResultExerciseRunFragment : Fragment() {
    private lateinit var binding : FragmentResultExerciseRunBinding
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog() }
    private var userActivity: UserActivity = UserActivity()
    private val viewModel : ResultExerciseRunViewModel by viewModels {
        ResultExerciseRunViewModelFactory((activity?.application as RunApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResultExerciseRunBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageCloseResultExerciseRunFragment.setOnClickListener {
           findNavController().popBackStack()
        }
        binding.cardViewSave.setOnClickListener {
            if(CheckConnection.haveNetworkConnection(requireContext())){
                showLoadingDialog()
                userActivity.setComment(binding.editTextAddNote.text.toString().trim())
                viewModel.updateResultRunExercise(userActivity, viewModel.getUserLocal()!!.getId())
            }else{
                Toast.makeText(requireContext(), "Your device does not have internet !", Toast.LENGTH_SHORT).show()
            }
        }
        binding.cardViewDelete.setOnClickListener {
            if(CheckConnection.haveNetworkConnection(requireContext())){
                showLoadingDialog()
                deleteRunExerciseRun()
            }else{
                Toast.makeText(requireContext(), "Your device does not have internet !", Toast.LENGTH_SHORT).show()
            }
        }
        binding.imageButtonEasy.setOnClickListener {
            viewModel.setUserFeel(1)
        }
        binding.imageButtonPerfect.setOnClickListener {
            viewModel.setUserFeel(2)
        }
        binding.imageButtonExhausted.setOnClickListener {
            viewModel.setUserFeel(3)
        }
        binding.imageViewResultExerRun.setOnClickListener {
            clickShowFullScreenImage()
        }
        subcribeToObservers()
        getUserActivityData()
    }

    private fun clickShowFullScreenImage() {
        if(userActivity.getRun()!!.img!!.isNotEmpty()){
            val bundle = Bundle()
            bundle.putString(Constants.URL_IMAGE, userActivity.getRun()!!.img)
            findNavController().navigate(R.id.action_resultExerciseRunFragment_to_fullScreenImageFragment, bundle)
        }
    }

    private fun showLoadingDialog(){
        loadingDialog.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
    }

    private fun deleteRunExerciseRun() {
        viewModel.deleteUserExercise(userActivity.getId())
    }

    private fun getUserActivityData() {
        val bundle = arguments
        bundle?.let {
            val id = it.getLong(Constants.ID_RECENT_EXERCISE)
            viewModel.getUserActivityDetail(id)
        }
    }

    private fun subcribeToObservers() {
        viewModel.userActivityDetail.observe(viewLifecycleOwner, Observer {
            bindDataToView(it)
            userActivity.setId(it.getId())
            userActivity.setRun(it.getRun()!!)
            userActivity.setActivityId(it.getActivity()!!.getId())
            userActivity.setMood(it.getMood())
        })

        viewModel.toast.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.success.observe(viewLifecycleOwner, Observer {
            if(it){
                loadingDialog.dismissDialog()
                findNavController().popBackStack()
            }else{
                loadingDialog.dismissDialog()
            }
        })

        viewModel.userFeel.observe(viewLifecycleOwner, Observer {
            userActivity.setMood(it)
            setUpUserFeelToView(it)
        })
    }

    private fun setUpUserFeelToView(feel : Int){
        when(feel){
            1 -> {
                binding.layoutEasy.setBackgroundColor(resources.getColor(R.color.grey_80))
                binding.layoutPerfect.setBackgroundColor(resources.getColor(R.color.white))
                binding.layoutExhausted.setBackgroundColor(resources.getColor(R.color.white))
            }
            2 -> {
                binding.layoutEasy.setBackgroundColor(resources.getColor(R.color.white))
                binding.layoutPerfect.setBackgroundColor(resources.getColor(R.color.grey_80))
                binding.layoutExhausted.setBackgroundColor(resources.getColor(R.color.white))
            }
            3 -> {
                binding.layoutEasy.setBackgroundColor(resources.getColor(R.color.white))
                binding.layoutPerfect.setBackgroundColor(resources.getColor(R.color.white))
                binding.layoutExhausted.setBackgroundColor(resources.getColor(R.color.grey_80))
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun bindDataToView(userActivityDetail: UserActivityDetail){
        userActivityDetail.getRun()?.let { run ->
            binding.textViewMovingTimeResultExerRun.text = "${TrackingUtil.getFormattedHour(run.timestamp - run.timeInMillis)} hrs"
            binding.textViewDistanceResultExerRun.text = "${run.distanceInKilometers / 1000f} km"
            binding.textViewCaloriesBurnedResultExerRun.text = "${run.caloriesBurned} Kcal"
            binding.textViewAverageSpeedResultExerRun.text = "${run.averageSpeedInKilometersPerHour} km/h"
            binding.textViewDurationResultExerRun.text = "${TrackingUtil.getFormattedTimer3(run.timeInMillis)} mins"

            if(run.img!!.isNotEmpty()){
                Picasso.get().load(run.img)
                    .error(R.drawable.ic_error_gif)
                    .placeholder(R.drawable.ic_loading_gif)
                    .into(binding.imageViewResultExerRun)
            }else{
                binding.imageViewResultExerRun.setImageResource(R.drawable.runner2)
            }
            userActivityDetail.getActivity()?.let { activity ->
                var duration = 0L
                for(i in 0 until activity.getWorkouts().size){
                    duration+= activity.getWorkouts()[i].getDuration()
                }
                binding.textViewTitleResultExerciseRunFragment.text = "${activity.getName()} - " +
                        "${TrackingUtil.getFormattedTimer2(duration, 2)} mins - ${if(activity.getType()==0) "Walking Exercise" else "Running Exercise"}"
                if(run.timeInMillis>= duration){
                    binding.imageViewIsCompletedResultExerRun.setImageResource(R.drawable.ic_completed)
                }else{
                    binding.imageViewIsCompletedResultExerRun.setImageResource(R.drawable.ic_not_completed)
                }
            }
        }
        binding.editTextAddNote.setText(userActivityDetail.getComment())
        setUpUserFeelToView(userActivityDetail.getMood())
    }
}