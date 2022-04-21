package vu.pham.runningappseminar.fragments

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import pub.devrel.easypermissions.EasyPermissions
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.databinding.FragmentExerciseRunBinding
import vu.pham.runningappseminar.services.Polyline
import vu.pham.runningappseminar.services.TrackingService
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.TrackingUtil

class ExerciseRunFragment : Fragment() {

    private lateinit var binding : FragmentExerciseRunBinding
    private var currentTimeInMillies=0L

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()

    private var weight:Float = 80f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExerciseRunBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestGPS()
        subscribeToObservers()

        binding.buttonStartExerciseRun.setOnClickListener {
            toggleRun()
        }
        binding.buttonStopExerciseRun.setOnClickListener {
            stopRun()
        }
    }

    private fun toggleRun(){
        if (isTracking){
            //nếu đang tracking mà bất nút run thì sẽ pause service lại
            sendCommandToService(Constants.ACTION_PAUSE_SERVICE)
        }else{
            // còn nếu ko tracking mà bất nút run thì sẽ start hoặc resume service
            sendCommandToService(Constants.ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun stopRun() {
        binding.textViewTimeCountExerciseRun.text = "00:00"
        sendCommandToService(Constants.ACTION_STOP_SERVICE)
        findNavController().popBackStack()
    }

    // đăng ký observers để lắng nghe sự thay đổi về data
    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })
        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            //addLatestPolyline()
            //moveCameraToUser()
        })
        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            currentTimeInMillies = it
            val formattedTimer = TrackingUtil.getFormattedTimer3(currentTimeInMillies, false)
            binding.textViewTimeCountExerciseRun.text = formattedTimer
            if(currentTimeInMillies > 0L){
                binding.buttonStopExerciseRun.visibility = View.VISIBLE
            }else{
                binding.buttonStopExerciseRun.visibility = View.GONE
            }
        })
    }

    private fun updateTracking(isTracking:Boolean){
        this.isTracking = isTracking
        if(!isTracking){
            binding.buttonStartExerciseRun.setImageResource(R.drawable.ic_play)
        }else if(isTracking){
            binding.buttonStartExerciseRun.setImageResource(R.drawable.ic_pause_2)
        }
    }

    private fun sendCommandToService(action:String){
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    }

    private fun requestGPS(){
        // nếu đã xin quyền rồi thì ko xin nữa
        if(TrackingUtil.hasLocationPermissions(requireContext())){
            return
        }
        //nếu chưa thì check version điện thoại, nếu nhỏ hơn android 10
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            //yêu cầu xin quyền
            EasyPermissions.requestPermissions(this,
                "Please accept location permissions to use this app.",
                Constants.REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
        }else{
            //yêu cầu xin quyền
            EasyPermissions.requestPermissions(this,
                "Please accept location permissions to use this app.",
                Constants.REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }
}