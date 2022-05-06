package vu.pham.runningappseminar.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.databinding.FragmentRunDetailBinding
import vu.pham.runningappseminar.models.Run
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.utils.TrackingUtil
import vu.pham.runningappseminar.viewmodels.RunDetailViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.RunDetailViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class RunDetailFragment : Fragment() {
    private lateinit var binding : FragmentRunDetailBinding
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

        getRunDetail()

        binding.imageCloseRunDetail.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getRunDetail() {
        val bundle = arguments
        bundle?.let {
            val id = it.getString(Constants.ID_RUN_DETAIL)
            viewModel.getRunDetail(id!!).observe(viewLifecycleOwner, Observer { run ->
                bindDataToView(run)
            })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindDataToView(run:Run){
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar2 = Calendar.getInstance()
        calendar2.timeInMillis = run.timestamp
        binding.textViewTitleRunDetail.text = "Run - ${dateFormat.format(calendar2.time)}"
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = run.timestamp - run.timeInMillis
        val dateFormat2 = SimpleDateFormat("hh:mm:ss", Locale.getDefault())
        binding.textViewMovingTimeRunDetail.text = "${dateFormat2.format(calendar.time)} hrs"
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
}