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
import vu.pham.runningappseminar.databinding.FragmentRunDetailBinding
import vu.pham.runningappseminar.models.Run
import vu.pham.runningappseminar.ui.utils.DialogFragmentRun
import vu.pham.runningappseminar.ui.utils.LoadingDialog
import vu.pham.runningappseminar.utils.*
import vu.pham.runningappseminar.viewmodels.RunDetailViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.RunDetailViewModelFactory

class RunDetailFragment : Fragment() {
    private lateinit var binding : FragmentRunDetailBinding
    private var runDelete: Run? = null
    private lateinit var loadingDialog: LoadingDialog
    private val viewModel : RunDetailViewModel by viewModels {
        RunDetailViewModelFactory((activity?.application as RunApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRunDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog()
        if(savedInstanceState!=null){
            val dialogFragmentRun = parentFragmentManager.findFragmentByTag(Constants.CANCEL_DELETE_RUN_DIALOG_TAG) as DialogFragmentRun?
            dialogFragmentRun?.setClickYes {
                deleteRun()
            }
        }

        getRunDetail()
        subcribeToObservers()

        binding.imageCloseRunDetail.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.imageDeteleRunDetail.setOnClickListener {
           showDialogCancel()
        }
    }
    private fun showLoadingDialog(){
        loadingDialog.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
    }

    private fun subcribeToObservers() {
        viewModel.successDelete.observe(viewLifecycleOwner, Observer {
            if(it){
                findNavController().popBackStack()
            }
            loadingDialog.dismissDialog()
        })
        viewModel.toastEvent.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()){
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteRun() {
        if(!CheckConnection.haveNetworkConnection(requireContext())){
            Toast.makeText(requireContext(), "Your device does not have internet. Please connect to the internet so that you can delete this run !", Toast.LENGTH_SHORT).show()
        }else{
            showLoadingDialog()
            if(runDelete!!.img!!.isNotEmpty()){
                val storageRef = viewModel.getFirebaseStorage()?.reference
                val desertRef = storageRef?.child("/image-${runDelete!!.id}.png")
                desertRef?.delete()?.addOnSuccessListener {
                    viewModel.deleteRunWithInternet(runDelete!!)
                }?.addOnFailureListener {
                    loadingDialog.dismissDialog()
                    Toast.makeText(requireContext(), "Error: "+it.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }else{
                viewModel.deleteRunWithInternet(runDelete!!)
            }
        }
    }

    private fun getRunDetail() {
        val bundle = arguments
        bundle?.let { itBundle ->
            val id = itBundle.getString(Constants.ID_RUN_DETAIL)
            viewModel.getRunDetail(id!!).observe(viewLifecycleOwner, Observer { run ->
                runDelete = run
                run?.let {
                    bindDataToView(it)
                }
            })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindDataToView(run:Run){
        binding.textViewTitleRunDetail.text = "Run - ${TrackingUtil.getFormattedDate(run.timestamp)}"
        binding.textViewMovingTimeRunDetail.text = "${TrackingUtil.getFormattedHour(run.timestamp - run.timeInMillis)} hrs"
        binding.textViewDistanceRunDetail.text = "${run.distanceInKilometers / 1000f} km"
        binding.textViewCaloriesBurnedRunDetail.text = "${run.caloriesBurned} Kcal"
        binding.textViewAverageSpeedRunDetail.text = "${run.averageSpeedInKilometersPerHour} km/h"
        binding.textViewDurationRunDetail.text = "${TrackingUtil.getFormattedTimer3(run.timeInMillis)} mins"
        if(run.img?.isEmpty()!!){
            binding.imageViewRunDetail.setImageResource(R.drawable.runner2)
        }else{
            Picasso.get().load(run.img)
                .error(R.drawable.ic_error_gif)
                .placeholder(R.drawable.ic_loading_gif)
                .into(binding.imageViewRunDetail)
        }
    }

    private fun showDialogCancel(){
        DialogFragmentRun("Delete this Run ?", "Are you sure to delete this run and the exercise related to it will be deleted too ?").apply {
            setClickYes {
                deleteRun()
            }
        }.show(parentFragmentManager, Constants.CANCEL_DELETE_RUN_DIALOG_TAG)
    }
}