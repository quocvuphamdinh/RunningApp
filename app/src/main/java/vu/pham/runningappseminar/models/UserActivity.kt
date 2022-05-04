package vu.pham.runningappseminar.models

class UserActivity() {
    private var id:Long=0L
    private var run: Run?=null
    private var activityId:Long=0L
    private var comment:String=""
    private var mood:Int=0

    constructor(run: Run, activityId: Long, comment: String, mood: Int) : this() {
        this.run = run
        this.activityId = activityId
        this.comment = comment
        this.mood = mood
    }

    fun getId():Long{
        return id
    }
    fun setId(id:Long){
        this.id = id
    }
    fun getRun(): Run?{
        return run
    }
    fun setRun(run: Run){
        this.run = run
    }
    fun getActivityId():Long{
        return activityId
    }
    fun setActivityId(activityId: Long){
        this.activityId = activityId
    }
    fun getComment():String{
        return comment
    }
    fun setComment(comment:String){
        this.comment = comment
    }
    fun getMood():Int{
        return mood
    }
    fun setMood(mood:Int){
        this.mood= mood
    }
}