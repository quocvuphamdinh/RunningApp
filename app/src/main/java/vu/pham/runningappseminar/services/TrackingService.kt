package vu.pham.runningappseminar.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.activity.RunActivity
import vu.pham.runningappseminar.utils.Constants.ACTION_PAUSE_SERVICE
import vu.pham.runningappseminar.utils.Constants.ACTION_SHOW_TRACKING_ACTIVITY
import vu.pham.runningappseminar.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import vu.pham.runningappseminar.utils.Constants.ACTION_STOP_SERVICE
import vu.pham.runningappseminar.utils.Constants.NOTIFICATION_CHANNEL_ID
import vu.pham.runningappseminar.utils.Constants.NOTIFICATION_CHANNEL_NAME
import vu.pham.runningappseminar.utils.Constants.NOTIFICATION_ID

class TrackingService : LifecycleService() {

    var isFirstRun = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent!=null){
            when(intent.action){
                ACTION_START_OR_RESUME_SERVICE ->{
                    if(isFirstRun){
                        startForegroundService()
                        isFirstRun = false
                    }else{
                        Log.d("serviceVu", "Resuming service...")
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Log.d("serviceVu", "Pause service")
                }
                ACTION_STOP_SERVICE -> {
                    Log.d("serviceVu", "Stop service")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService(){
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