package vu.pham.runningappseminar.models

class Workout {
    private var id:Long=0L
    private var name:String=""
    private var duration:Long=0

    constructor(name: String, duration: Long) {
        this.name = name
        this.duration = duration
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
    fun setName(duration:Long){
        this.duration = duration
    }
    fun getDuration():Long{
        return duration
    }
    fun setDuration(duration: Long){
        this.duration = duration
    }
}
