package vu.pham.runningappseminar.database

import android.graphics.Bitmap

//@Entity(tableName = "running_table")
class Run {
    //@PrimaryKey(autoGenerate = true)
    private var id:Int?=null
    private var image:Bitmap?=null
    private var timestamp:Long=0L // khoảng thời gian khi nào chạy
    private var averageSpeedInKilometers:Float=0F
    private var distanceInKilometers:Int=0
    private var timeInMillis:Long=0L // chạy trong bao lâu
    private var caloriesBurned:Int =0

    constructor(
        image: Bitmap?,
        timestamp: Long,
        averageSpeedInKilometers: Float,
        distanceInKilometers: Int,
        timeInMillis: Long,
        caloriesBurned: Int
    ) {
        this.image = image
        this.timestamp = timestamp
        this.averageSpeedInKilometers = averageSpeedInKilometers
        this.distanceInKilometers = distanceInKilometers
        this.timeInMillis = timeInMillis
        this.caloriesBurned = caloriesBurned
    }
}