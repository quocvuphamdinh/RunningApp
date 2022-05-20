package vu.pham.runningappseminar.ui.fragments

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.ui.adapters.RecyclerViewMusicAdapter
import vu.pham.runningappseminar.databinding.BottomSheetDialogMusicBinding
import vu.pham.runningappseminar.databinding.FragmentExerciseRunBinding
import vu.pham.runningappseminar.models.*
import vu.pham.runningappseminar.services.TrackingService
import vu.pham.runningappseminar.ui.utils.DialogFragmentRun
import vu.pham.runningappseminar.ui.utils.LoadingDialog
import vu.pham.runningappseminar.utils.*
import vu.pham.runningappseminar.viewmodels.ExerciseRunViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.ExerciseRunViewModelFactory
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.math.round

class ExerciseRunFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding : FragmentExerciseRunBinding
    private lateinit var bindingBottomSheet : BottomSheetDialogMusicBinding
    private lateinit var musicAdapter: RecyclerViewMusicAdapter
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaPlayerNotifi: MediaPlayer
    private lateinit var mediaPlayerFinish: MediaPlayer
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var runnable: Runnable
    private var handler = Handler()
    private var map : GoogleMap? = null
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
        binding.mapViewExerciseRun.onCreate(savedInstanceState)
        if(savedInstanceState!=null){
            val dialogFragmentRun = parentFragmentManager.findFragmentByTag(Constants.CANCEL_RUNNING_DIALOG_TAG) as DialogFragmentRun?
            dialogFragmentRun?.setClickYes {
                showLoadingDialog()
                stopRun(true)
            }
        }
        requestGPS()
        subscribeToObservers()
        getDataExerciseAndBindDataToView()
        //music
        mediaPlayerNotifi = MediaPlayer.create(requireContext(), R.raw.notification)
        mediaPlayerFinish = MediaPlayer.create(requireContext(), R.raw.finish)
        initMusicAdapter(viewModel.musicList.value!!)
        setUpBottomSheetMusic()
        initMediaPlayer(viewModel.musicList.value!![viewModel.positionMusic].file)
        setUpSeekBar()
        //
        loadingDialog = LoadingDialog()

        binding.mapViewExerciseRun.getMapAsync {
            map = it
        }

        binding.buttonStartExerciseRun.setOnClickListener {
            sendCommandToService(Constants.ACTION_PAUSE_SERVICE)
            toggleRun()
        }
        binding.buttonStopExerciseRun.setOnClickListener {
            showLoadingDialog()
            sendCommandToService(Constants.ACTION_PAUSE_SERVICE)
            zoomToSeeWholeTrack()
            saveDataExerciseRun()
        }
        binding.layoutPlayMusic.setOnClickListener {
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

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if(viewModel.currentTimeInMillies>0L){
                sendCommandToService(Constants.ACTION_PAUSE_SERVICE)
                showCancelRunningDialog()
            }else{
                if(mediaPlayer.isPlaying){
                    mediaPlayer.stop()
                }
                findNavController().popBackStack()
            }
        }
    }
    private fun showLoadingDialog(){
        loadingDialog.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
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
        mediaPlayer = MediaPlayer.create(requireContext(), file)
        bindingBottomSheet.textViewEndTimeMusic.text = TrackingUtil.getFormattedDurationMusic(mediaPlayer.duration)
        bindingBottomSheet.seekBarMusic.max = mediaPlayer.duration
        bindingBottomSheet.textViewMusicName.text = viewModel.musicList.value!![viewModel.positionMusic].name
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

    private fun setUpBottomSheetMusic(){
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bindingBottomSheet = DataBindingUtil.inflate(layoutInflater, R.layout.bottom_sheet_dialog_music, null, false)
        bottomSheetDialog.setContentView(bindingBottomSheet.root)
        setUpRecyclerMusic()
    }

    private fun setUpRecyclerMusic(){
        bindingBottomSheet.rcvMusic.layoutManager = LinearLayoutManager(context)
        bindingBottomSheet.rcvMusic.adapter = musicAdapter
        bindingBottomSheet.rcvMusic.setHasFixedSize(true)
    }

    override fun onResume() {
        super.onResume()
        binding.mapViewExerciseRun.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapViewExerciseRun.onStart()
    }

    override fun onPause() {
        super.onPause()
        binding.mapViewExerciseRun.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapViewExerciseRun.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapViewExerciseRun.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapViewExerciseRun.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapViewExerciseRun.onSaveInstanceState(outState)
    }

    private fun moveCameraToUser(){
        if(viewModel.pathPoints.isNotEmpty() && viewModel.pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(viewModel.pathPoints.last().last(), Constants.MAP_ZOOM))
        }
    }
    // pathpoint gồm những đường(Polyline) được kết nối với nhau bằng những tọa độ(LatLng)
    //function thêm các đường Polyline vào pathpoint
    private fun addLatestPolyline(){
        // nếu đường pathPoints ko rỗng và đường polyline ở trong pathpoints có ít nhất 2 item trở lên
        if(viewModel.pathPoints.isNotEmpty() && viewModel.pathPoints.last().size > 1) {
            val preLastLatLng =viewModel.pathPoints.last()[viewModel.pathPoints.last().size - 2]// lấy tọa độ kế cuối trong ds
            val lastLatLng = viewModel.pathPoints.last().last()// lấy tọa độ cuối cùng
            val polylineOption = PolylineOptions()
                .color(Constants.POLYLINE_COLOR) // thêm màu cho đường polyline
                .width(Constants.POLYLINE_WIDTH) // độ rộng cho đường polyline
                .add(preLastLatLng)
                .add(lastLatLng)//kết nối
            map?.addPolyline(polylineOption)
        }
    }

    private fun zoomToSeeWholeTrack(){
        val bounds = LatLngBounds.Builder()
        for (polyline in viewModel.pathPoints){
            for (position in polyline){
                bounds.include(position)
            }
        }

        map?.moveCamera(CameraUpdateFactory.newLatLngBounds(
            bounds.build(),
            binding.mapViewExerciseRun.width,
            binding.mapViewExerciseRun.height,
            (binding.mapViewExerciseRun.height * 0.05f).toInt()
        ))
    }

    private fun uploadImageRunToServer(bitmap: Bitmap, run:Run, userActivity: UserActivity) {
        val storageRef = viewModel.getFirebaseStorage()?.reference
        val nameHinh= "image-${run.id}"
        val nameHinh2= "$nameHinh.png"
        val mountainsRef = storageRef?.child(nameHinh2)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = mountainsRef?.putBytes(data)
        uploadTask?.addOnFailureListener {
            Toast.makeText(requireContext(), "Error uploaded image run !", Toast.LENGTH_SHORT).show()
        }?.addOnSuccessListener { _ ->
            mountainsRef.downloadUrl.addOnSuccessListener { uri ->
                run.isRunWithExercise = 1
                run.img = uri.toString()
                viewModel.insertUserExercise(userActivity, user?.getId()!!, run)
            }
        }
    }

    private fun goToResultRunExercise(userActivity: UserActivity){
        val bundle = Bundle()
        bundle.putLong(Constants.ID_RECENT_EXERCISE, userActivity.getId())
        findNavController().navigate(R.id.action_exerciseRunFragment_to_resultExerciseRunFragment, bundle)
    }

    private fun saveDataExerciseRun(){
        map?.snapshot { bitmap ->
            user = viewModel.getUserFromSharedPref()
            val dateTimestamp = Calendar.getInstance().timeInMillis
            for(polyline in viewModel.pathPoints){
                viewModel.distanceInMeters += TrackingUtil.calculatePolylineLength(polyline).toInt()
            }
            val run = Run("${user?.getUsername()}${user?.getPassword()}${dateTimestamp}", dateTimestamp, viewModel.averageSpeed,
                viewModel.distanceInMeters, viewModel.currentTimeInMillies, viewModel.caloriesBurned, "", 0)
            val userActivity = UserActivity(run, id!!, "", 0)
            if(CheckConnection.haveNetworkConnection(requireContext())){
                uploadImageRunToServer(bitmap!!, run, userActivity)
            }else{
                viewModel.insertRunLocal(run)
            }
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
        DialogFragmentRun("Cancel the Run ?", "Are you sure to cancel this run and the data will be deleted ?").apply {
            setClickYes {
                showLoadingDialog()
                stopRun(true)
            }
        }.show(parentFragmentManager, Constants.CANCEL_RUNNING_DIALOG_TAG)
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

    private fun stopRun(isPop:Boolean) {
        viewModel.resetData()
        binding.textViewTimeCountExerciseRun.text = "00:00"
        binding.textViewDistanceExerciseRun.text = "0"
        binding.textViewAverageSpeedExerciseRun.text ="0.00"
        binding.textViewCaloriesBurnedExerciseRun.text = "0"
        binding.textViewTimeLeftExerciseRun.text = "Time left - 00:00"
        binding.progressBarExerciseRun.progress = 0
        if(mediaPlayer.isPlaying){
            mediaPlayer.stop()
        }
        if(mediaPlayerNotifi.isPlaying){
            mediaPlayerNotifi.stop()
        }
        loadingDialog.dismissDialog()
        sendCommandToService(Constants.ACTION_STOP_SERVICE)
        mediaPlayerFinish.start()
        if(isPop){
            findNavController().popBackStack()
        }
    }

    // đăng ký observers để lắng nghe sự thay đổi về data
    private fun subscribeToObservers(){
        viewModel.musicList.observe(viewLifecycleOwner, Observer {
            musicAdapter.submitList(it)
            setUpRecyclerMusic()
        })
        viewModel.music.observe(viewLifecycleOwner, Observer {
            bindingBottomSheet.textViewMusicName.text = it.name
            if(mediaPlayer.isPlaying){
                mediaPlayer.stop()
            }
            initMediaPlayer(it.file)
            mediaPlayer.start()
        })
        viewModel.success.observe(viewLifecycleOwner, Observer {
            if(it){
                Toast.makeText(requireContext(), "Save run in local successfully !!", Toast.LENGTH_SHORT).show()
                stopRun(true)
            }
        })

        viewModel.userActivity.observe(viewLifecycleOwner, Observer {
            if(it.getId()!=0L){
                stopRun(false)
                goToResultRunExercise(it)
            }
        })

        viewModel.toastEvent.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })

        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            viewModel.pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
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
                        if(it <viewModel.workours.size-1){
                            mediaPlayerNotifi.start()
                        }
                        viewModel.lastCurrentTime += viewModel.workours[it].getDuration()
                        viewModel.updateIndex()
                    }
                    if(it >= viewModel.workours.size && !isFinishFirstTime){
                        isFinishFirstTime = true
                        showLoadingDialog()
                        sendCommandToService(Constants.ACTION_PAUSE_SERVICE)
                        saveDataExerciseRun()
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