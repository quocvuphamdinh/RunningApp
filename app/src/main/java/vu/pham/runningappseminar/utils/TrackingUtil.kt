package vu.pham.runningappseminar.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Build
import pub.devrel.easypermissions.EasyPermissions
import vu.pham.runningappseminar.services.Polyline
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object TrackingUtil {

    //ACCESS COARSE LOCATION sẽ trả cho bạn địa chỉ dựa trên các thông tin về 3G hoặc Wifi.
    //Không cần bật GPS.
    //Còn FINE thì bạn phải bật GPS. Nếu bạn không cần tracking hoặc không cần địa chỉ chính xác
    //thì không cần dùng đến FINE LOCATION.

    fun hasLocationPermissions(context: Context): Boolean {
        //nếu điện thoại có version nhỏ hơn android 10
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            //yêu cầu quyền location FINE và COARSE
            return EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            //nếu là android 10 hoặc lớn hơn
        } else {
            // yêu cầu thêm quyền BACKGROUND_LOCATION
            return EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    fun calculatePolylineLength(polyline: Polyline):Float{
        var distance = 0F
        for (i in 0..polyline.size - 2){
            val pos1 = polyline[i]
            val pos2 = polyline[i + 1]

            val result = FloatArray(1)
            Location.distanceBetween(pos1.latitude, pos1.longitude,
            pos2.latitude, pos2.longitude,
            result)

            distance +=result[0]
        }
        return distance
    }

    fun getFormattedTimer(ms: Long, includeMillis:Boolean = false) : String{
        var milliseconds =ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -=TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        val f: NumberFormat = DecimalFormat("00")
        if(!includeMillis){
            return "${f.format(hours)}:${f.format(minutes)}:${f.format(seconds)}"
        }
        milliseconds -= TimeUnit.SECONDS.toMillis(seconds)
        milliseconds /=10
        return "${f.format(hours)}:${f.format(minutes)}:${f.format(seconds)}:${f.format(milliseconds)}"
    }

    fun getFormattedTimer3(ms: Long, includeMillis:Boolean = false) : String{
        var milliseconds =ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -=TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        val f: NumberFormat = DecimalFormat("00")
        if(!includeMillis){
            return "${f.format(minutes)}:${f.format(seconds)}"
        }
        milliseconds -= TimeUnit.SECONDS.toMillis(seconds)
        milliseconds /=10
        return "${f.format(minutes)}:${f.format(seconds)}:${f.format(milliseconds)}"
    }

    fun getFormattedTimer2(ms: Long, option:Int = 0) : String{
        var milliseconds =ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -=TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        val f: NumberFormat = DecimalFormat("00")
        milliseconds -= TimeUnit.SECONDS.toMillis(seconds)
        milliseconds /=10
        if(option==1){
            return f.format(hours)
        }else if(option==2){
            return f.format(minutes)
        }else if(option==3){
            return f.format(seconds)
        }else if(option==4){
            return f.format(milliseconds)
        }
        return "${f.format(hours)}:${f.format(minutes)}:${f.format(seconds)}:${f.format(milliseconds)}"
    }

    fun getFormattedDate(date : Long) : String{
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
    fun getFormattedHour(hour : Long) : String{
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = hour
        val dateFormat = SimpleDateFormat("hh:mm:ss", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
    @SuppressLint("SimpleDateFormat")
    fun getFormattedDurationMusic(value:Int):String{
        val simpleDateFormat = SimpleDateFormat("mm:ss")
        return simpleDateFormat.format(value)
    }
}