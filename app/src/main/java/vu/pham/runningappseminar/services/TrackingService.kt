package vu.pham.runningappseminar.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.activity.RunActivity
import vu.pham.runningappseminar.utils.Constants.ACTION_PAUSE_SERVICE
import vu.pham.runningappseminar.utils.Constants.ACTION_SHOW_TRACKING_ACTIVITY
import vu.pham.runningappseminar.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import vu.pham.runningappseminar.utils.Constants.ACTION_STOP_SERVICE
import vu.pham.runningappseminar.utils.Constants.FASTEST_LOCATION_INTERVAL
import vu.pham.runningappseminar.utils.Constants.LOCATION_UPDATE_INTERVAL
import vu.pham.runningappseminar.utils.Constants.NOTIFICATION_CHANNEL_ID
import vu.pham.runningappseminar.utils.Constants.NOTIFICATION_CHANNEL_NAME
import vu.pham.runningappseminar.utils.Constants.NOTIFICATION_ID
import vu.pham.runningappseminar.utils.Constants.TIMER_UPDATE_INTERVAL
import vu.pham.runningappseminar.utils.TrackingUtil

//Polyline là hình nhiều đường, nhiều điểm liên kết với nhau tạo thành 1 đường hay hình khối
typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class TrackingService : LifecycleService() {

    var isFirstRun = true
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var timeRunInSecond = MutableLiveData<Long>()
    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L // tổng thời gian chạy được
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    companion object{
        var timeRunInMillis = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>() // ds các đường Polyline
    }

    private fun startTimer(){
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!){
                // là khoảng thời gian giữa giờ hiện tại và giờ bắt đầu
                lapTime = System.currentTimeMillis() - timeStarted
                timeRunInMillis.postValue(timeRun + lapTime)
                //nếu timeRun đã đi hơn 1s(1000ms) rồi thì tăng timeRunSecond lên 1 giây rồi cứ như thế đến 2, 3...giây
                if(timeRunInMillis.value!! >= lastSecondTimestamp + 1000L){
                    timeRunInSecond.postValue(timeRunInSecond.value!! +1)
                    lastSecondTimestamp += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL) // sau 0.05(50L) giây thì update timer lên main thread
            }
            timeRun +=lapTime // khoảng thời gian đã chạy dc
        }
    }

    private fun pauseService(){
        isTracking.postValue(false)
        isTimerEnabled = false
    }

    override fun onCreate() {
        super.onCreate()
        initialValue()//khởi tạo position
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        isTracking.observe(this, Observer {
            updateLocationTracking(it) // observer quan sát mỗi khi isTracking thay đổi giá trị thì update
        })
    }
    private fun initialValue(){
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf()) // khởi tạo list rỗng
        timeRunInSecond.postValue(0L)
        timeRunInMillis.postValue(0L)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent!=null){
            when(intent.action){
                ACTION_START_OR_RESUME_SERVICE ->{
                    if(isFirstRun){
                        startForegroundService()
                        isFirstRun = false
                    }else{
                        Log.d("serviceVu", "Resuming service...")
                        startTimer()
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Log.d("serviceVu", "Pause service")
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    Log.d("serviceVu", "Stop service")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking:Boolean){
        if(isTracking){
            if (TrackingUtil.hasLocationPermissions(this@TrackingService)){
                val request = LocationRequest().apply {
                    interval= LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY // muốn nhận kết quả location chính xác nhất
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request, locationCallback, Looper.getMainLooper()
                )
            }
        }else{
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    val locationCallback = object : LocationCallback(){
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if(isTracking.value!!){
                result?.locations?.let { locations -> // đổi tên it thành locations
                    for (location in locations) {
                        addPathPoint(location)
                        Log.d("tracking", "New Location: ${location.latitude}, ${location.longitude}")
                    }
                }
            }
        }
    }
    private fun addPathPoint(location: Location?){
        location?.let {
            val position = LatLng(location.latitude, location.longitude) // lấy vị trí trên map
            pathPoints.value?.apply {
                last().add(position) // thêm vào sau item cuối cùng
                pathPoints.postValue(this)//gán giá trị mới
            }
        }
    }
    private fun addEmptyPolyline(){
        pathPoints.value?.apply {
            add(mutableListOf())
            pathPoints.postValue(this)
        }?: pathPoints.postValue(mutableListOf(mutableListOf()))
    }

    private fun startForegroundService(){
        startTimer()
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //kiểm tra version điện thoại có lớn hơn hoặc bằng Android Oreo hay không thì tạo 1 notification channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this@TrackingService, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)//tránh người dùng click vào bị mất notification
            .setOngoing(true) // tránh người dùng vuốt để xóa notification
            .setSmallIcon(R.drawable.runner)
            .setContentTitle("Running App của Vũ")
            .setContentText("00:00:00")
            .setContentIntent(getRunningActivityPendingIntent()) // click vào sẽ nhảy vào run activity

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getRunningActivityPendingIntent():PendingIntent{
        return PendingIntent.getActivity(
            this@TrackingService,
            0,
            Intent(this@TrackingService, RunActivity::class.java)
                .also {
                    it.action = ACTION_SHOW_TRACKING_ACTIVITY
                }, FLAG_UPDATE_CURRENT) // FLAG_UPDATE_CURRENT là khi pendingintent đã tồn tại rồi nó sẽ khởi động hoặc update lại mà ko tạo cái mới

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }
}