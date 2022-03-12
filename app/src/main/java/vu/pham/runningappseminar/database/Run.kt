package vu.pham.runningappseminar.database

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "running_table")
class Run {
    @PrimaryKey(autoGenerate = true)
     var id:Int?=null
     var timestamp:Long=0L // khoảng thời gian khi nào chạy
     var averageSpeedInKilometersPerHour:Float=0F
     var distanceInKilometers:Int=0
     var timeInMillis:Long=0L // chạy trong bao lâu
     var caloriesBurned:Int =0

    constructor(
        timestamp: Long,
        averageSpeedInKilometersPerHour: Float,
        distanceInKilometers: Int,
        timeInMillis: Long,
        caloriesBurned: Int,
    ) {
        this.timestamp = timestamp
        this.averageSpeedInKilometersPerHour = averageSpeedInKilometersPerHour
        this.distanceInKilometers = distanceInKilometers
        this.timeInMillis = timeInMillis
        this.caloriesBurned = caloriesBurned
    }
}