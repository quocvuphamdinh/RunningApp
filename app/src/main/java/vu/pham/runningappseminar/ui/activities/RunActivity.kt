package vu.pham.runningappseminar.ui.activities

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.models.Run
import vu.pham.runningappseminar.databinding.ActivityRunBinding
import vu.pham.runningappseminar.databinding.BottomSheetDialogMusicBinding
import vu.pham.runningappseminar.models.Music
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.services.Polyline
import vu.pham.runningappseminar.services.TrackingService
import vu.pham.runningappseminar.ui.adapters.RecyclerViewMusicAdapter
import vu.pham.runningappseminar.ui.utils.DialogFragmentRun
import vu.pham.runningappseminar.ui.utils.LoadingDialog
import vu.pham.runningappseminar.utils.*
import vu.pham.runningappseminar.utils.Constants.ACTION_PAUSE_SERVICE
import vu.pham.runningappseminar.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import vu.pham.runningappseminar.utils.Constants.ACTION_STOP_SERVICE
import vu.pham.runningappseminar.utils.Constants.MAP_ZOOM
import vu.pham.runningappseminar.utils.Constants.POLYLINE_COLOR
import vu.pham.runningappseminar.utils.Constants.POLYLINE_WIDTH
import vu.pham.runningappseminar.viewmodels.RunViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.RunViewModelFactory
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.math.round

class RunActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding:ActivityRunBinding
    private lateinit var bindingBottomSheet : BottomSheetDialogMusicBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var musicAdapter: RecyclerViewMusicAdapter
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var runnable: Runnable
    private var handler = Handler()
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
        setTheme(R.style.Theme_RunningAppSeminar)
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_run)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_run)

        loadingDialog = LoadingDialog()
        if(savedInstanceState!=null){
            val dialogFragmentRun = supportFragmentManager.findFragmentByTag(Constants.CANCEL_RUNNING_DIALOG_TAG) as DialogFragmentRun?
            dialogFragmentRun?.setClickYes {
                stopRun()
            }
        }
        user = viewModel.getUserFromSharedPref()
        weight = user?.getWeight()?.toFloat() ?: 80f
        //music
        initMusicAdapter(viewModel.musicList.value!!)
        setUpBottomSheetMusic()
        initMediaPlayer(viewModel.musicList.value!![viewModel.positionMusic].file)
        setUpSeekBar()
        //anhXa()
        binding.mapView.onCreate(savedInstanceState)
        requestGPS()
        initGoogleMap()
        subscribeToObservers()
        clickRun()
        clickStopRun()
        clickToClose()
        binding.textViewPlayMusicRun.setOnClickListener {
            bottomSheetDialog.show()
        }
        bindingBottomSheet.imagePlayMusic.setOnClickListener {
            if(!mediaPlayer.isPlaying){
                viewModel.initMusicIsPlaying()
                mediaPlayer.start()
                bindingBottomSheet.imagePlayMusic.setImageResource(R.drawable.ic_pause_2)
            }else{
                mediaPlayer.pause()
                bindingBottomSheet.imagePlayMusic.setImageResource(R.drawable.ic_play)
            }
        }
        bindingBottomSheet.imageSkipNextMusic.setOnClickListener {
            goToNextSong(true)
        }
        bindingBottomSheet.imageSkipPreviousMusic.setOnClickListener {
            goToNextSong(false)
        }
    }
    private fun goToNextSong(option:Boolean){
        if(option){
            bindingBottomSheet.seekBarMusic.progress = bindingBottomSheet.seekBarMusic.progress +5000
            if(bindingBottomSheet.seekBarMusic.progress >= bindingBottomSheet.seekBarMusic.max){
                bindingBottomSheet.seekBarMusic.progress = 0
                viewModel.positionMusic++
                if(viewModel.positionMusic > viewModel.musicList.value!!.size-1){
                    viewModel.positionMusic = 0
                }
                viewModel.goNextMusic(viewModel.positionMusic)
            }
        }else{
            bindingBottomSheet.seekBarMusic.progress = bindingBottomSheet.seekBarMusic.progress - 5000
            if(bindingBottomSheet.seekBarMusic.progress <=0){
                viewModel.positionMusic--
                if(viewModel.positionMusic < 0){
                    viewModel.positionMusic = 0
                }
                viewModel.goNextMusic(viewModel.positionMusic)
            }
        }
        mediaPlayer.seekTo(bindingBottomSheet.seekBarMusic.progress)
        bindingBottomSheet.textViewStartTimeMusic.text = TrackingUtil.getFormattedDurationMusic(mediaPlayer.currentPosition)
    }
    private fun setUpSeekBar() {
        bindingBottomSheet.seekBarMusic.progress = 0
        bindingBottomSheet.seekBarMusic.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    mediaPlayer.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        runnable = Runnable {
            bindingBottomSheet.seekBarMusic.progress = mediaPlayer.currentPosition
            bindingBottomSheet.textViewStartTimeMusic.text = TrackingUtil.getFormattedDurationMusic(mediaPlayer.currentPosition)
            handler.postDelayed(runnable, 1000)
            mediaPlayer.setOnCompletionListener {
                bindingBottomSheet.seekBarMusic.progress = 0
                viewModel.positionMusic++
                if(viewModel.positionMusic > viewModel.musicList.value!!.size-1){
                    viewModel.positionMusic = 0
                }
                viewModel.goNextMusic(viewModel.positionMusic)
            }
        }
        handler.postDelayed(runnable, 1000)
    }
    private fun initMediaPlayer(file:Int) {
        mediaPlayer = MediaPlayer.create(this@RunActivity, file)
        bindingBottomSheet.textViewEndTimeMusic.text = TrackingUtil.getFormattedDurationMusic(mediaPlayer.duration)
        bindingBottomSheet.seekBarMusic.max = mediaPlayer.duration
        bindingBottomSheet.textViewMusicName.text = viewModel.musicList.value!![viewModel.positionMusic].name
    }
    private fun setUpBottomSheetMusic(){
        bottomSheetDialog = BottomSheetDialog(this@RunActivity)
        bindingBottomSheet = DataBindingUtil.inflate(layoutInflater, R.layout.bottom_sheet_dialog_music, null, false)
        bottomSheetDialog.setContentView(bindingBottomSheet.root)
        setUpRecyclerMusic()
    }
    private fun setUpRecyclerMusic(){
        bindingBottomSheet.rcvMusic.layoutManager = LinearLayoutManager(this@RunActivity)
        bindingBottomSheet.rcvMusic.adapter = musicAdapter
        bindingBottomSheet.rcvMusic.setHasFixedSize(true)
    }
    private fun initMusicAdapter(list:List<Music>){
        musicAdapter = RecyclerViewMusicAdapter(object : RecyclerViewMusicAdapter.ClickMusic{
            override fun clickItem(music: Music) {
                val position = viewModel.getMusicPosition(music)
                if(position!=viewModel.positionMusic){
                    viewModel.positionMusic = position
                    viewModel.goNextMusic(viewModel.positionMusic)
                    if(!mediaPlayer.isPlaying) {
                        bindingBottomSheet.imagePlayMusic.setImageResource(R.drawable.ic_pause_2)
                    }
                }
            }
        })
        musicAdapter.submitList(list)
    }
    private fun showLoadingDialog(){
        loadingDialog.show(supportFragmentManager, Constants.LOADING_DIALOG_TAG)
    }

    private fun clickStopRun() {
        binding.textViewStopRun.setOnClickListener {
            showLoadingDialog()
            sendCommandToService(ACTION_PAUSE_SERVICE)
            zoomToSeeWholeTrack()
            saveRunToDatabase()
        }
    }

    //show Alertdialog th??ng b??o khi ng?????i d??ng h???y running
    private fun showCancelRunningDialog(){
        DialogFragmentRun("Cancel the Run ?", "Are you sure to cancel this run and the data will be deleted ?").apply {
            setClickYes {
                stopRun()
            }
        }.show(supportFragmentManager, Constants.CANCEL_RUNNING_DIALOG_TAG)
    }

    private fun stopRun() {
        loadingDialog.dismissDialog()
        sendCommandToService(ACTION_STOP_SERVICE)
        currentTimeInMillies = 0L
        binding.textViewTimeCountRun.text = "00:00:00:00"
        if(mediaPlayer.isPlaying){
            mediaPlayer.stop()
        }
        finish()
    }

    // ????ng k?? observers ????? l???ng nghe s??? thay ?????i v??? data
    private fun subscribeToObservers(){
        viewModel.musicList.observe(this, Observer {
            musicAdapter.submitList(it)
            setUpRecyclerMusic()
        })
        viewModel.music.observe(this, Observer {
            bindingBottomSheet.textViewMusicName.text = it.name
            if(mediaPlayer.isPlaying){
                mediaPlayer.stop()
            }
            initMediaPlayer(it.file)
            mediaPlayer.start()
        })
        viewModel.success.observe(this, Observer {
            if(it){
                stopRun()
            }
        })
        viewModel.toastEvent.observe(this, Observer {
            Toast.makeText(this@RunActivity, it, Toast.LENGTH_LONG).show()
        })

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
            //n???u ??ang tracking m?? b???t n??t run th?? s??? pause service l???i
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }else{
            // c??n n???u ko tracking m?? b???t n??t run th?? s??? start ho???c resume service
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
        googleMap?.snapshot { bitmap ->
            var distanceInMeters = 0
            for(polyline in pathPoints){
                distanceInMeters += TrackingUtil.calculatePolylineLength(polyline).toInt()
            }
            // distanceInMeters / 1000f : chia ????? l???y km
            //currentTimeInMillies / 1000f : l???y second
            // currentTimeInMillies / 1000f / 60 : l???y minute
            // currentTimeInMillies / 1000f / 60 / 60 : l???y hour
            // v???n t???c b???ng qu??ng ???????ng chia cho th???i gian
            val avgSpeed = round((distanceInMeters / 1000f) / (currentTimeInMillies / 1000f / 60 / 60) *10) / 10f
            val dateTimestamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
            val run = Run("${user?.getUsername()}${user?.getPassword()}${dateTimestamp}",
                dateTimestamp, avgSpeed, distanceInMeters, currentTimeInMillies, caloriesBurned, "", 0)
            if(!CheckConnection.haveNetworkConnection(this@RunActivity)){
                viewModel.insertRun(run)
            }else{
                uploadImageRunToServer(bitmap!!, run)
            }
        }
    }

    private fun uploadImageRunToServer(bitmap: Bitmap, run:Run) {
        val storageRef = viewModel.getFirebaseStorage()?.reference
        val nameHinh= "image-${run.id}"
        val nameHinh2= "$nameHinh.png"
        val mountainsRef = storageRef?.child(nameHinh2)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = mountainsRef?.putBytes(data)
        uploadTask?.addOnFailureListener {
            Toast.makeText(this@RunActivity, "Error uploaded image run !", Toast.LENGTH_SHORT).show()
            Log.d("hivu", it.toString())
        }?.addOnSuccessListener { _ ->
            mountainsRef.downloadUrl.addOnSuccessListener { uri ->
                run.img = uri.toString()
                viewModel.insertRun(run)
            }
        }
    }

    //function n??y ????? v??? l???i t???t c??? c??c ???????ng(Polyline) khi ng?????i d??ng tho??t t???m th???i app
    private fun addAllPolylines(){
        for (polyline in pathPoints){
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            googleMap?.addPolyline(polylineOptions)
        }
    }

    // pathpoint g???m nh???ng ???????ng(Polyline) ???????c k???t n???i v???i nhau b???ng nh???ng t???a ?????(LatLng)
    //function th??m c??c ???????ng Polyline v??o pathpoint
    private fun addLatestPolyline(){
        // n???u ???????ng pathPoints ko r???ng v?? ???????ng polyline ??? trong pathpoints c?? ??t nh???t 2 item tr??? l??n
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]// l???y t???a ????? k??? cu???i trong ds
            val lastLatLng = pathPoints.last().last()// l???y t???a ????? cu???i c??ng
            val polylineOption = PolylineOptions()
                .color(POLYLINE_COLOR) // th??m m??u cho ???????ng polyline
                .width(POLYLINE_WIDTH) // ????? r???ng cho ???????ng polyline
                .add(preLastLatLng)
                .add(lastLatLng)//k???t n???i
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
        initGoogleMap()
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
        TrackingService.isRunOnly = true
        Intent(this@RunActivity, TrackingService::class.java).also {
            it.action = action
            this@RunActivity.startService(it)
        }
    }
    private fun requestGPS(){
        // n???u ???? xin quy???n r???i th?? ko xin n???a
        if(TrackingUtil.hasLocationPermissions(this@RunActivity)){
            return
        }
        //n???u ch??a th?? check version ??i???n tho???i, n???u nh??? h??n android 10
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            //y??u c???u xin quy???n
            EasyPermissions.requestPermissions(this@RunActivity,
            "Please accept location permissions to use this app.",
            Constants.REQUEST_CODE_LOCATION_PERMISSION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION)
        }else{
            //y??u c???u xin quy???n
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
                sendCommandToService(ACTION_PAUSE_SERVICE)
                showCancelRunningDialog()
            }else{
                if(mediaPlayer.isPlaying){
                    mediaPlayer.stop()
                }
                finish()
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        //n???u y??u c???u xin quy???n b??? t??? ch???i v??nh vi???n
        if(EasyPermissions.somePermissionPermanentlyDenied(this@RunActivity, perms)){
            //show 1 dialog c???nh b??o b???t bu???c y??u c???u quy???n
            AppSettingsDialog.Builder(this@RunActivity).build().show()
        }else{
            //n???u ko th?? y??u c???u quy???n ti???p
            requestGPS()
        }
    }

    // h??m nh???n k???t qu??? c???a y??u c???u quy???n
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this@RunActivity)
    }

    override fun onBackPressed() {
        if(currentTimeInMillies > 0){
            sendCommandToService(ACTION_PAUSE_SERVICE)
            showCancelRunningDialog()
        }else{
            if(mediaPlayer.isPlaying){
                mediaPlayer.stop()
            }
            finish()
        }
    }
}