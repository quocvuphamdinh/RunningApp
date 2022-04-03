package vu.pham.runningappseminar.model

class Activity() {
    private var id:Long=0L
    private var name:String=""
    private var type:Int=1
    private var durationOfWorkouts:Int=0
    private var workouts:List<Workout> = ArrayList()

    constructor(name: String, type: Int, workouts: List<Workout>) : this() {
        this.name = name
        this.type = type
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

    fun getWorkouts():List<Workout>{
        return workouts
    }
    fun setWorkouts(workouts: List<Workout>){
        this.workouts = workouts
    }
    fun getDurationOfWorkouts():Int{
        return durationOfWorkouts
    }

    override fun equals(other: Any?): Boolean {
        other as Activity
        if(id!=other.id){
            return false
        }
        if(name!=other.name){
            return false
        }
        if(type!=other.type){
            return false
        }
        if(durationOfWorkouts!=other.durationOfWorkouts){
            return false
        }
        if(workouts!=other.workouts){
            return false
        }
        return true
    }
}


