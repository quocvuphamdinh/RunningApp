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
import vu.pham.runningappseminar.activities.MainActivity
import vu.pham.runningappseminar.activities.RunActivity
import vu.pham.runningappseminar.utils.Constants.ACTION_PAUSE_SERVICE
import vu.pham.runningappseminar.utils.Constants.ACTION_SHOW_EXERCISE_RUN_FRAGMENT
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

    private var isFirstRun = true
    private var stopService = false

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentNotificationBuilder: NotificationCompat.Builder
    private lateinit var notificationBuilder: NotificationCompat.Builder

    private var timeRunInSecond = MutableLiveData<Long>()
    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L // tổng thời gian chạy được
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    companion object{
        var isRunOnly = true
        var timeRunInMillis = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>() // ds các đường Polyline
    }

    //cập nhật timer value cho notification
    private fun updateNotificationUpdateState(isTracking: Boolean){
        val notificationActionText = if(isTracking) "Pause" else "Resume"
        //khi click vào pause hoặc resume
        val pendingIntent = if(isTracking){
            //nếu đang chạy mà bấm thì pause
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)
        }else{
            //nếu ko chạy mà bấm thì resume
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent, FLAG_UPDATE_CURRENT)
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // xóa các noti cũ để update cái mới
        currentNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true // cho phép thay đổi
            set(currentNotificationBuilder, ArrayList<NotificationCompat.Action>()) // set giá trị mới, để arraylist rỗng vào để xóa cái cũ
        }

        if(!stopService){
            //thêm nút và sự kiện start và pause
            currentNotificationBuilder = notificationBuilder
                .addAction(R.drawable.ic_pause, notificationActionText, pendingIntent)
            //tiến hành update noti
            notificationManager.notify(NOTIFICATION_ID, currentNotificationBuilder.build())
        }
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
            timeRun +=lapTime // khoảng thời gian đã chạy dc và đã chạy đến đâu để khi pause rồi resume sẽ đếm tiếp
        }
    }

    private fun pauseService(){
        isTracking.postValue(false)
        isTimerEnabled = false
    }

    override fun onCreate() {
        super.onCreate()
        initialValue()//khởi tạo
        notificationBuilder = NotificationCompat.Builder(this@TrackingService, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)//tránh người dùng click vào bị mất notification
            .setOngoing(true) // tránh người dùng vuốt để xóa notification
            .setSmallIcon(R.drawable.runner)
            .setContentTitle("Running App của Vũ")
            .setContentText("00:00:00")
            .setContentIntent(getRunningActivityPendingIntent()) // click vào sẽ nhảy vào run activity
        currentNotificationBuilder = notificationBuilder
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        isTracking.observe(this, Observer {
            updateLocationTracking(it) // observer quan sát mỗi khi isTracking thay đổi giá trị thì update
            updateNotificationUpdateState(it)
        })
    }
    private fun initialValue(){
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf()) // khởi tạo list rỗng
        timeRunInSecond.postValue(0L)
        timeRunInMillis.postValue(0L)
        lapTime = 0L
        timeRun = 0L
        lastSecondTimestamp = 0L
        timeStarted = 0L
    }

    private fun stopService(){
        stopService = true
        isFirstRun = true
        pauseService()
        initialValue()
        stopForeground(true) // gỡ noti
        stopSelf() //kill service
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
                        //calculateDataRun()
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Log.d("serviceVu", "Pause service")
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    Log.d("serviceVu", "Stop service")
                    stopService()
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
        //calculateDataRun()
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //kiểm tra version điện thoại có lớn hơn hoặc bằng Android Oreo hay không thì tạo 1 notification channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }


        startForeground(NOTIFICATION_ID, notificationBuilder.build())

        //lắng nghe sự thay đổi timer ở dịnh dạng giây để cập nhật
        timeRunInSecond.observe(this, Observer {
           if(!stopService){
               val notification = currentNotificationBuilder
                   .setContentText(TrackingUtil.getFormattedTimer(it*1000L, false))
               //tiến hành update noti
               notificationManager.notify(NOTIFICATION_ID, notification.build())
           }
        })
    }

    private fun getRunningActivityPendingIntent():PendingIntent{
        return PendingIntent.getActivity(
            this@TrackingService,
            0,
            Intent(this@TrackingService, if(isRunOnly) RunActivity::class.java else MainActivity::class.java)
                .also {
                    it.action = if(isRunOnly) ACTION_SHOW_TRACKING_ACTIVITY else ACTION_SHOW_EXERCISE_RUN_FRAGMENT
                }, FLAG_UPDATE_CURRENT) // FLAG_UPDATE_CURRENT là khi pendingintent đã tồn tại rồi nó sẽ khởi động hoặc update lại mà ko tạo cái mới

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }
}