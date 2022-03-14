package vu.pham.runningappseminar.utils

import android.graphics.Color

object Constants {
    const val REQUEST_CODE_LOCATION_PERMISSION =0
    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_SHOW_TRACKING_ACTIVITY="ACTION_SHOW_TRACKING_ACTIVITY"
    const val NOTIFICATION_CHANNEL_ID="tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME="TrackingVu"
    const val NOTIFICATION_ID=1
    const val LOCATION_UPDATE_INTERVAL = 5000L //5s update location 1 lần
    const val FASTEST_LOCATION_INTERVAL = 2000L
    const val POLYLINE_COLOR = Color.RED // màu đường
    const val POLYLINE_WIDTH = 8f// độ dài
    const val MAP_ZOOM = 18f // độ zoom của camera khi trỏ tới user
    const val TIMER_UPDATE_INTERVAL = 50L
    const val DATABASE_NAME = "Running App Seminar Vu"
    const val INTENT_SET_MYGOAL="my_goal"
}