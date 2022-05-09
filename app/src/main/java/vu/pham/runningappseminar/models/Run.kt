package vu.pham.runningappseminar.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_table")
class Run {
    @PrimaryKey(autoGenerate = false)
     var id:String=""
     var timestamp:Long=0L // khoảng thời gian khi nào chạy
     var averageSpeedInKilometersPerHour:Float=0F
     var distanceInKilometers:Int=0
     var timeInMillis:Long=0L // chạy trong bao lâu
     var caloriesBurned:Int =0
     var img: String? = null
    var isRunWithExercise: Int = 0 // 0 là tự run, còn 1 là run with exercise

    constructor(
        id:String,
        timestamp: Long,
        averageSpeedInKilometersPerHour: Float,
        distanceInKilometers: Int,
        timeInMillis: Long,
        caloriesBurned: Int,
        img: String,
        isRunWithExercise: Int
    ) {
        this.id = id
        this.timestamp = timestamp
        this.averageSpeedInKilometersPerHour = averageSpeedInKilometersPerHour
        this.distanceInKilometers = distanceInKilometers
        this.timeInMillis = timeInMillis
        this.caloriesBurned = caloriesBurned
        this.img = img
        this.isRunWithExercise = isRunWithExercise
    }
}