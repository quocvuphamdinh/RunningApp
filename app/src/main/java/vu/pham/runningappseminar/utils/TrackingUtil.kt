package vu.pham.runningappseminar.utils

import android.Manifest
import android.content.Context
import android.os.Build
import pub.devrel.easypermissions.EasyPermissions
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.TimeUnit

object TrackingUtil {

    //ACCESS COARSE LOCATION sẽ trả cho bạn địa chỉ dựa trên các thông tin về 3G hoặc Wifi.
    //Không cần bật GPS.
    //Còn FINE thi bạn phải bật GPS. Nếu bạn không cần tracking hoặc không cần địa chỉ chính xác
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
}