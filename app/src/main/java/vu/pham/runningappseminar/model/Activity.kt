package vu.pham.runningappseminar.model

class Activity() {
    private var id:Long=0L
    private var name:String=""
    private var type:Int=1
    private var isUseGPS:Int=1
    private var workouts:ArrayList<Workout> = ArrayList()
    private var durationOfWorkouts:Int=0

    constructor(name: String, type: Int, isUseGPS: Int, workouts: ArrayList<Workout>) : this() {
        this.name = name
        this.type = type
        this.isUseGPS = isUseGPS
        this.workouts = workouts
    }
    fun getId():Long{
        return id
    }
    fun setId(id:Long){
        this.id = id
    }
    fun getName():String{
        return name
    }
    fun setName(name:String){
        this.name = name
    }
    fun getType():Int{
        return type
    }
    fun setType(type:Int){
        this.type = type
    }
    fun getIsUseGPS():Int{
        return isUseGPS
    }
    fun setIsUseGPS(isUseGPS: Int){
        this.isUseGPS = isUseGPS
    }
    fun getWorkouts():ArrayList<Workout>{
        return workouts
    }
    fun setWorkouts(workouts: ArrayList<Workout>){
        this.workouts = workouts
    }
    fun getDurationOfWorkouts():Int{

        for (i in 0 until workouts.size){
            durationOfWorkouts += workouts[i].getDuration()
        }
        return durationOfWorkouts
    }


}


