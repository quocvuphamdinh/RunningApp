package vu.pham.runningappseminar.activities

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.database.local.Run
import vu.pham.runningappseminar.databinding.ActivityRunBinding
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.services.Polyline
import vu.pham.runningappseminar.services.TrackingService
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.Constants.ACTION_PAUSE_SERVICE
import vu.pham.runningappseminar.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import vu.pham.runningappseminar.utils.Constants.ACTION_STOP_SERVICE
import vu.pham.runningappseminar.utils.Constants.MAP_ZOOM
import vu.pham.runningappseminar.utils.Constants.POLYLINE_COLOR
import vu.pham.runningappseminar.utils.Constants.POLYLINE_WIDTH
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.utils.TrackingUtil
import vu.pham.runningappseminar.viewmodels.RunViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.RunViewModelFactory
import java.util.*
import kotlin.math.round

class RunActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
//    private lateinit var txtStopRun:TextView
//    private lateinit var btnRun:MaterialButton
//    private lateinit var imgClose:ImageView
//    private lateinit var mapView: MapView
//    private lateinit var txtTimeRun:TextView
    private lateinit var binding:ActivityRunBinding
    private var googleMap: GoogleMap?=null
    private var currentTimeInMillies=0L

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()

    private val viewModel :RunViewModel by viewModels{
        RunViewModelFactory((application as RunApplication).repository)
    }

    private var user:User? =null
    private var weight:Float = 80f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_run)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_run)

        user = viewModel.getUserFromSharedPref()
        weight = user?.getWeight()?.toFloat() ?: 80f
        //anhXa()
        binding.mapView.onCreate(savedInstanceState)
        requestGPS()
        initGoogleMap()
        subscribeToObservers()
        clickRun()
        clickStopRun()
        clickToClose()
    }

    private fun clickStopRun() {
        binding.textViewStopRun.setOnClickListener {
            zoomToSeeWholeTrack()
            saveRunToDatabase()
        }
    }

    //show Alertdialog thông báo khi người dùng hủy running
    private fun showCancelRunningDialog(){
        val dialog = MaterialAlertDialogBuilder(this@RunActivity, R.style.AlertDialogTheme)
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

    private fun stopRun() {
        binding.textViewTimeCountRun.text = "00:00:00:00"
        sendCommandToService(ACTION_STOP_SERVICE)
        finish()
    }

    // đăng ký observers để lắng nghe sự thay đổi về data
    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(this, Observer {
            updateTracking(it)
        })
        TrackingService.pathPoints.observe(this, Observer {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })
        TrackingService.timeRunInMillis.observe(this, Observer {
            currentTimeInMillies = it
            val formattedTimer = TrackingUtil.getFormattedTimer(currentTimeInMillies, true)
            binding.textViewTimeCountRun.text = formattedTimer
            if(currentTimeInMillies > 0L){
                binding.textViewStopRun.visibility = View.VISIBLE
            }else{
                binding.textViewStopRun.visibility = View.INVISIBLE
            }
        })
    }

    private fun toggleRun(){
        if (isTracking){
            //nếu đang tracking mà bất nút run thì sẽ pause service lại
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }else{
            // còn nếu ko tracking mà bất nút run thì sẽ start hoặc resume service
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun updateTracking(isTracking:Boolean){
        this.isTracking = isTracking
        if(!isTracking && currentTimeInMillies<0L){
            binding.buttonRun.text = "START"
        }else if (!isTracking && currentTimeInMillies>0L){
            binding.buttonRun.text = "RESUME"
        }else if(isTracking){
            binding.buttonRun.text = "PAUSE"
        }
    }
    private fun moveCameraToUser(){
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(pathPoints.last().last(), MAP_ZOOM))
        }
    }

    private fun zoomToSeeWholeTrack(){
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints){
            for (position in polyline){
                bounds.include(position)
            }
        }

        googleMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(
            bounds.build(),
            binding.mapView.width,
            binding.mapView.height,
            (binding.mapView.height * 0.05f).toInt()
        ))
    }

    private fun saveRunToDatabase(){
        var distanceInMeters = 0
        for(polyline in pathPoints){
            distanceInMeters += TrackingUtil.calculatePolylineLength(polyline).toInt()
        }
        // distanceInMeters / 1000f : chia để lấy km
        //currentTimeInMillies / 1000f : lấy second
        // currentTimeInMillies / 1000f / 60 : lấy minute
        // currentTimeInMillies / 1000f / 60 / 60 : lấy hour
        // vận tốc bằng quãng đường chia cho chiều cao
        val avgSpeed = round((distanceInMeters / 1000f) / (currentTimeInMillies / 1000f / 60 / 60) *10) / 10f
        val dateTimestamp = Calendar.getInstance().timeInMillis
        val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
        val run = Run("${user?.getUsername()}${user?.getPassword()}${dateTimestamp}",dateTimestamp, avgSpeed, distanceInMeters, currentTimeInMillies, caloriesBurned)

        viewModel.insertRun(run)

        Toast.makeText(this@RunActivity, "Run saved successfully !!", Toast.LENGTH_LONG).show()
        stopRun()
    }

    //function này để vẽ lại tất cả các đường(Polyline) khi người dùng thoát tạm thời app
    private fun addAllPolylines(){
        for (polyline in pathPoints){
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            googleMap?.addPolyline(polylineOptions)
        }
    }

    // pathpoint gồm những đường(Polyline) được kết nối với nhau bằng những tọa độ(LatLng)
    //function thêm các đường Polyline vào pathpoint
    private fun addLatestPolyline(){
        // nếu đường pathPoints ko rỗng và đường polyline ở trong pathpoints có ít nhất 2 item trở lên
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]// lấy tọa độ kế cuối trong ds
            val lastLatLng = pathPoints.last().last()// lấy tọa độ cuối cùng
            val polylineOption = PolylineOptions()
                .color(POLYLINE_COLOR) // thêm màu cho đường polyline
                .width(POLYLINE_WIDTH) // độ rộng cho đường polyline
                .add(preLastLatLng)
                .add(lastLatLng)//kết nối
            googleMap?.addPolyline(polylineOption)
        }
    }
    private fun clickRun() {
        binding.buttonRun.setOnClickListener {
            toggleRun()
        }
    }

    private fun initGoogleMap() {
        binding.mapView.getMapAsync {
            googleMap = it
            addAllPolylines()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    private fun sendCommandToService(action:String){
        Intent(this@RunActivity, TrackingService::class.java).also {
            it.action = action
            this@RunActivity.startService(it)
        }
    }
    private fun requestGPS(){
        // nếu đã xin quyền rồi thì ko xin nữa
        if(TrackingUtil.hasLocationPermissions(this@RunActivity)){
            return
        }
        //nếu chưa thì check version điện thoại, nếu nhỏ hơn android 10
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            //yêu cầu xin quyền
            EasyPermissions.requestPermissions(this@RunActivity,
            "Please accept location permissions to use this app.",
            Constants.REQUEST_CODE_LOCATION_PERMISSION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION)
        }else{
            //yêu cầu xin quyền
            EasyPermissions.requestPermissions(this@RunActivity,
                "Please accept location permissions to use this app.",
                Constants.REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    private fun clickToClose() {
        binding.imageViewCloseRunningActivity.setOnClickListener {
            if(currentTimeInMillies > 0){
                showCancelRunningDialog()
            }else{
                finish()
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        //nếu yêu cầu xin quyền bị từ chối vĩnh viễn
        if(EasyPermissions.somePermissionPermanentlyDenied(this@RunActivity, perms)){
            //show 1 dialog cảnh báo bắt buộc yêu cầu quyền
            AppSettingsDialog.Builder(this@RunActivity).build().show()
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
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this@RunActivity)
    }
}