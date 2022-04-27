package vu.pham.runningappseminar.fragments

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.database.local.Run
import vu.pham.runningappseminar.databinding.FragmentExerciseRunBinding
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.models.UserActivity
import vu.pham.runningappseminar.models.Workout
import vu.pham.runningappseminar.services.TrackingService
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.utils.TrackingUtil
import vu.pham.runningappseminar.viewmodels.ExerciseRunViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.ExerciseRunViewModelFactory
import java.util.*
import kotlin.math.round

class ExerciseRunFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding : FragmentExerciseRunBinding
    private var isFinishFirstTime = false
    private var user: User? =null
    private var id:Long?=null
    private val viewModel : ExerciseRunViewModel by viewModels{
        ExerciseRunViewModelFactory((activity?.application as RunApplication).repository)
    }

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
        getDataExerciseAndBindDataToView()

        binding.buttonStartExerciseRun.setOnClickListener {
            sendCommandToService(Constants.ACTION_PAUSE_SERVICE)
            toggleRun()
        }
        binding.buttonStopExerciseRun.setOnClickListener {
            saveDataExerciseRun()
        }

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if(viewModel.currentTimeInMillies>0L){
                sendCommandToService(Constants.ACTION_PAUSE_SERVICE)
                showCancelRunningDialog()
            }else{
                findNavController().popBackStack()
            }
        }
    }

    private fun saveDataExerciseRun(){
        user = viewModel.getUserFromSharedPref()
        val dateTimestamp = Calendar.getInstance().timeInMillis
        for(polyline in viewModel.pathPoints){
            viewModel.distanceInMeters += TrackingUtil.calculatePolylineLength(polyline).toInt()
        }
        val run = Run("${user?.getUsername()}${user?.getPassword()}${dateTimestamp}", dateTimestamp, viewModel.averageSpeed,
            viewModel.distanceInMeters, viewModel.currentTimeInMillies, viewModel.caloriesBurned)
        val userActivity = UserActivity(run, id!!, "", 0)
        lifecycleScope.launch {
            viewModel.insertRunLocal(run)
            val result = viewModel.insertUserExercise(userActivity, user?.getId()!!)
            Toast.makeText(requireContext(), "id: ${result.getId()}-activityId: ${result.getActivityId()}-Mood: ${result.getMood()}-Comment: ${result.getComment()}", Toast.LENGTH_LONG).show()
            stopRun()
        }
    }

    private fun getDataExerciseAndBindDataToView() {
        val bundle = arguments
        viewModel.workours = bundle?.getParcelableArrayList<Workout>(Constants.DURATION_EXERCISE)!!
        id = bundle.getLong(Constants.ID_EXERCISE)
        var duration = 0L
        for (i in 0 until viewModel.workours.size){
            duration +=viewModel.workours[i].getDuration()
        }

        TrackingService.initTimeLeft(duration)
        binding.progressBarExerciseRun.max = (duration.toInt()/100)
        binding.textViewNumberExerciseRun.text = "1/${viewModel.workours.size}"
    }

    private fun showCancelRunningDialog(){
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel the Run ?")
            .setMessage("Are you sure to cancel this run and the data will be deleted ?")
            .setIcon(R.drawable.ic_warning)
            .setPositiveButton("Yes", object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    stopRun()
                }
            })
            .setNegativeButton("No", object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog?.cancel()
                }
            })
        dialog.show()
    }

    private fun toggleRun(){
        if (viewModel.isTracking){
            //nếu đang tracking mà bất nút run thì sẽ pause service lại
            sendCommandToService(Constants.ACTION_PAUSE_SERVICE)
        }else{
            // còn nếu ko tracking mà bất nút run thì sẽ start hoặc resume service
            sendCommandToService(Constants.ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun stopRun() {
        viewModel.resetData()
        binding.textViewTimeCountExerciseRun.text = "00:00"
        binding.textViewDistanceExerciseRun.text = "0"
        binding.textViewAverageSpeedExerciseRun.text ="0.00"
        binding.textViewCaloriesBurnedExerciseRun.text = "0"
        binding.textViewTimeLeftExerciseRun.text = "Time left - 00:00"
        binding.progressBarExerciseRun.progress = 0
        sendCommandToService(Constants.ACTION_STOP_SERVICE)
        findNavController().popBackStack()
    }

    // đăng ký observers để lắng nghe sự thay đổi về data
    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            viewModel.pathPoints = it
            for(polyline in viewModel.pathPoints){
                viewModel.distanceInMeters += TrackingUtil.calculatePolylineLength(polyline).toInt()
            }
            viewModel.caloriesBurned = ((viewModel.distanceInMeters / 1000f) * viewModel.weight).toInt()
            viewModel.averageSpeed = round((viewModel.distanceInMeters / 1000f) / (viewModel.currentTimeInMillies / 1000f / 60 / 60) *10) / 10f
            if(viewModel.distanceInMeters!=0){
                binding.textViewAverageSpeedExerciseRun.text = viewModel.averageSpeed.toString()
                binding.textViewDistanceExerciseRun.text = (viewModel.distanceInMeters / 1000f).toString()
                binding.textViewCaloriesBurnedExerciseRun.text = viewModel.caloriesBurned.toString()
            }
            viewModel.distanceInMeters = 0
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {times->
            if(viewModel.isTracking){
                viewModel.currentTimeInMillies = times
                val formattedTimer = TrackingUtil.getFormattedTimer3(viewModel.currentTimeInMillies, false)
                binding.textViewTimeCountExerciseRun.text = formattedTimer
                binding.progressBarExerciseRun.progress = (times.toInt()/100)
            }

            viewModel.index.observe(viewLifecycleOwner, Observer {
                if(viewModel.isTracking){
                    if(it <viewModel.workours.size){
                        binding.textViewNameEachExercise.text = "${viewModel.workours[it].getName().toUpperCase()}"
                        binding.textViewNumberExerciseRun.text = "${it+1}/${viewModel.workours.size}"
                    }
                    if(it< viewModel.workours.size && viewModel.currentTimeInMillies >= (viewModel.workours[it].getDuration()+viewModel.lastCurrentTime)){
                        viewModel.lastCurrentTime += viewModel.workours[it].getDuration()
                        viewModel.updateIndex()
                    }
                    if(it >= viewModel.workours.size && !isFinishFirstTime){
                        isFinishFirstTime = true
                        stopRun()
                    }
                }
            })

            if(viewModel.currentTimeInMillies > 0L){
                binding.buttonStopExerciseRun.visibility = View.VISIBLE
            }else{
                binding.buttonStopExerciseRun.visibility = View.GONE
            }
        })
        TrackingService.timeLeft.observe(viewLifecycleOwner, Observer {
            Log.d("times", it.toString())
            binding.textViewTimeLeftExerciseRun.text = "Time left - ${TrackingUtil.getFormattedTimer3(it)}"
        })
    }

    private fun updateTracking(isTracking:Boolean){
        viewModel.isTracking = isTracking
        if(!isTracking){
            binding.buttonStartExerciseRun.setImageResource(R.drawable.ic_play)
        }else if(isTracking){
            binding.buttonStartExerciseRun.setImageResource(R.drawable.ic_pause_2)
        }
    }

    private fun sendCommandToService(action:String){
        TrackingService.isRunOnly = false
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

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        //nếu yêu cầu xin quyền bị từ chối vĩnh viễn
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            //show 1 dialog cảnh báo bắt buộc yêu cầu quyền
            AppSettingsDialog.Builder(this).build().show()
        }else{
            //nếu ko thì yêu cầu quyền tiếp
            requestGPS()
        }
    }

    // hàm nhận kết quả của yêu cầu quyền
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

}