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
import vu.pham.runningappseminar.ui.activities.HomeActivity
import vu.pham.runningappseminar.ui.activities.MainActivity
import vu.pham.runningappseminar.ui.activities.RunActivity
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

//Polyline l?? h??nh nhi???u ???????ng, nhi???u ??i???m li??n k???t v???i nhau t???o th??nh 1 ???????ng hay h??nh kh???i
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
    private var timeRun = 0L // t???ng th???i gian ch???y ???????c
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    companion object{
        var isRunOnly = true
        var timeRunInMillis = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>() // ds c??c ???????ng Polyline

        val timeLeft = MutableLiveData<Long>()
        fun initTimeLeft(value:Long){
            timeLeft.postValue(value)
        }
    }

    //c???p nh???t timer value cho notification
    private fun updateNotificationUpdateState(isTracking: Boolean){
        val notificationActionText = if(isTracking) "Pause" else "Resume"
        //khi click v??o pause ho???c resume
        val pendingIntent = if(isTracking){
            //n???u ??ang ch???y m?? b???m th?? pause
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)
        }else{
            //n???u ko ch???y m?? b???m th?? resume
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent, FLAG_UPDATE_CURRENT)
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // x??a c??c noti c?? ????? update c??i m???i
        currentNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true // cho ph??p thay ?????i
            set(currentNotificationBuilder, ArrayList<NotificationCompat.Action>()) // set gi?? tr??? m???i, ????? arraylist r???ng v??o ????? x??a c??i c??
        }

        if(!stopService){
            //th??m n??t v?? s??? ki???n start v?? pause
            currentNotificationBuilder = notificationBuilder
                .addAction(R.drawable.ic_pause, notificationActionText, pendingIntent)
            //ti???n h??nh update noti
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
                // l?? kho???ng th???i gian gi???a gi??? hi???n t???i v?? gi??? b???t ?????u
                lapTime = System.currentTimeMillis() - timeStarted
                timeRunInMillis.postValue(timeRun + lapTime)
                //n???u timeRun ???? ??i h??n 1s(1000ms) r???i th?? t??ng timeRunSecond l??n 1 gi??y r???i c??? nh?? th??? ?????n 2, 3...gi??y
                if(timeRunInMillis.value!! >= lastSecondTimestamp + 1000L){
                    timeRunInSecond.postValue(timeRunInSecond.value!! +1)
                    lastSecondTimestamp += 1000L
                    if(!isRunOnly){
                        timeLeft.postValue(timeLeft.value!!- 1000L)
                    }
                }
                delay(TIMER_UPDATE_INTERVAL) // sau 0.05(50L) gi??y th?? update timer l??n main thread
            }
            timeRun +=lapTime // kho???ng th???i gian ???? ch???y dc v?? ???? ch???y ?????n ????u ????? khi pause r???i resume s??? ?????m ti???p
        }
    }

    private fun pauseService(){
        isTracking.postValue(false)
        isTimerEnabled = false
    }

    override fun onCreate() {
        super.onCreate()
        initialValue()//kh???i t???o
        notificationBuilder = NotificationCompat.Builder(this@TrackingService, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)//tr??nh ng?????i d??ng click v??o b??? m???t notification
            .setOngoing(true) // tr??nh ng?????i d??ng vu???t ????? x??a notification
            .setSmallIcon(R.drawable.runner)
            .setContentTitle("Running App c???a V??")
            .setContentText("00:00:00")
            .setContentIntent(getRunningActivityPendingIntent()) // click v??o s??? nh???y v??o run activity
        currentNotificationBuilder = notificationBuilder
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        isTracking.observe(this, Observer {
            updateLocationTracking(it) // observer quan s??t m???i khi isTracking thay ?????i gi?? tr??? th?? update
            updateNotificationUpdateState(it)
        })
    }
    private fun initialValue(){
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf()) // kh???i t???o list r???ng
        timeRunInSecond.postValue(0L)
        timeRunInMillis.postValue(0L)
        lapTime = 0L
        timeRun = 0L
        lastSecondTimestamp = 0L
        timeStarted = 0L
    }

    private fun stopService(){
        if(!isRunOnly){
            timeLeft.postValue(0)
        }
        stopService = true
        isFirstRun = true
        pauseService()
        initialValue()
        stopForeground(true) // g??? noti
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
                    interval= LOCATION_UPDATE_INTERVAL // l???y t???a ????? li??n t???c m???i l???n 5s
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY // mu???n nh???n k???t qu??? location ch??nh x??c nh???t
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
                result?.locations?.let { locations -> // ?????i t??n it th??nh locations
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
            val position = LatLng(location.latitude, location.longitude) // l???y v??? tr?? tr??n map
            pathPoints.value?.apply {
                last().add(position) // th??m v??o sau item cu???i c??ng
                pathPoints.postValue(this)//g??n gi?? tr??? m???i
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

        //ki???m tra version ??i???n tho???i c?? l???n h??n ho???c b???ng Android Oreo hay kh??ng th?? t???o 1 notification channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }


        startForeground(NOTIFICATION_ID, notificationBuilder.build())

        //l???ng nghe s??? thay ?????i timer ??? d???nh d???ng gi??y ????? c???p nh???t
        timeRunInSecond.observe(this, Observer {
           if(!stopService){
               val notification = currentNotificationBuilder
                   .setContentText(TrackingUtil.getFormattedTimer(it*1000L, false))
               //ti???n h??nh update noti
               notificationManager.notify(NOTIFICATION_ID, notification.build())
           }
        })
    }

    private fun getRunningActivityPendingIntent():PendingIntent{
        return PendingIntent.getActivity(
            this@TrackingService,
            0,
            Intent(this@TrackingService, if(isRunOnly) RunActivity::class.java else HomeActivity::class.java)
                .also {
                    it.action = if(isRunOnly) ACTION_SHOW_TRACKING_ACTIVITY else ACTION_SHOW_EXERCISE_RUN_FRAGMENT
                }, FLAG_UPDATE_CURRENT) // FLAG_UPDATE_CURRENT l?? khi pendingintent ???? t???n t???i r???i n?? s??? kh???i ?????ng ho???c update l???i m?? ko t???o c??i m???i

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }
}