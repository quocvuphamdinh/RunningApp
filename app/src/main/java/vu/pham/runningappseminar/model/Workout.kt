package vu.pham.runningappseminar.model

class Workout {
    private var id:Long=0L
    private var name:String=""
    private var duration:Int=0

    constructor(name: String, duration: Int) {
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
    fun setName(duration:Int){
        this.duration = duration
    }
    fun getDuration():Int{
        return duration
    }
    fun setDuration(duration: Int){
        this.duration = duration
    }
}
