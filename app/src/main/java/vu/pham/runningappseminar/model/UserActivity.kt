package vu.pham.runningappseminar.model

class UserActivity() {
    private var id:Long=0L
    private var run:Run= Run()
    private var activity:Activity=Activity()
    private var comment:String=""
    private var mood:Int=0

    constructor(run: Run, activity: Activity, comment: String, mood: Int) : this() {
        this.run = run
        this.activity = activity
        this.comment = comment
        this.mood = mood
    }

    fun getId():Long{
        return id
    }
    fun setId(id:Long){
        this.id = id
    }
    fun getRun():Run{
        return run
    }
    fun setRun(run: Run){
        this.run = run
    }
    fun getActivity():Activity{
        return activity
    }
    fun setActivity(activity: Activity){
        this.activity = activity
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