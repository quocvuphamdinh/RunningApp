package vu.pham.runningappseminar.activity

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.material.button.MaterialButton
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.services.TrackingService
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import vu.pham.runningappseminar.utils.TrackingUtil

class RunActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private lateinit var btnRun:MaterialButton
    private lateinit var imgClose:ImageView
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run)

        anhXa()
        mapView.onCreate(savedInstanceState)
        requestGPS()
        initGoogleMap()
        clickRun()
        clickToClose()
    }

    private fun clickRun() {
        btnRun.setOnClickListener {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun initGoogleMap() {
        mapView.getMapAsync {
            googleMap = it
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
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
        imgClose.setOnClickListener {
            finish()
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

    private fun anhXa() {
        imgClose = findViewById(R.id.imageViewCloseRunningActivity)
        mapView = findViewById(R.id.mapView)
        btnRun = findViewById(R.id.buttonRun)
    }
}