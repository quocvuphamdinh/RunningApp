package vu.pham.runningappseminar.models

import java.util.*

class Run() {
    private var id:Long=0L
    private var avgSpeed:Double=0.0
    private var caloriesBurned:Int=0
    private var distanceInKMH:Double=0.0
    private var runTime:Date= Date(System.currentTimeMillis())
    private var timeInMillies:Date= Date(System.currentTimeMillis())
    private var dayOfWeek:Date = Date(System.currentTimeMillis())
    private var user:User=User()

    constructor(
        avgSpeed: Double,
        caloriesBurned: Int,
        distanceInKMH: Double,
        runTime: Date,
        timeInMillies: Date,
        dayOfWeek: Date,
        user: User
    ) : this() {
        this.avgSpeed = avgSpeed
        this.caloriesBurned = caloriesBurned
        this.distanceInKMH = distanceInKMH
        this.runTime = runTime
        this.timeInMillies = timeInMillies
        this.dayOfWeek = dayOfWeek
        this.user = user
    }


    fun getId():Long{
        return id
    }
    fun setId(id:Long){
        this.id = id
    }
    fun getAvgSpeed():Double{
        return avgSpeed
    }
    fun setAvgSpeed(avgSpeed:Double){
        this.avgSpeed = avgSpeed
    }
    fun getCaloriesBurned():Int{
        return caloriesBurned
    }
    fun setCaloriesBurned(caloriesBurned:Int){
        this.caloriesBurned = caloriesBurned
    }
    fun getDistanceInKMH():Double{
        return distanceInKMH
    }
    fun setDistanceInKMH(distanceInKMH:Double){
        this.distanceInKMH = distanceInKMH
    }
    fun getRunTime():Date{
        return runTime
    }
    fun setRunTime(runTime:Date){
        this.runTime = runTime
    }
    fun getTimeInMillies():Date{
        return timeInMillies
    }
    fun setTimeInMillies(timeInMillies:Date){
        this.timeInMillies = timeInMillies
    }
    fun getDayOfWeek():Date{
        return dayOfWeek
    }
    fun setDayOfWeek(dayOfWeek:Date){
        this.dayOfWeek = dayOfWeek
    }
    fun getUser():User{
        return user
    }
    fun setUser(user:User){
        this.user = user
    }
}