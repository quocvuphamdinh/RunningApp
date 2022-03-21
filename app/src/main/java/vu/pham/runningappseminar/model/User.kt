package vu.pham.runningappseminar.model

import java.io.Serializable

class User() : Serializable{
    private var id:Long=0L
    private var userName:String=""
    private var passWord:String=""
    private var fullName:String=""
    private var avartar:String=""
    private var gender:String=""
    private var weight:Int=0
    private var height:Int=0
    private var distanceGoal:Long=0L

    constructor(username: String, password: String, fullname: String, gender :String, height:Int, weight:Int, distanceGoal:Long) : this() {
        this.userName = username
        this.passWord = password
        this.fullName = fullname
        this.gender = gender
        this.weight = weight
        this.height = height
        this.distanceGoal = distanceGoal
    }

    fun getdistanceGoal():Long{
        return distanceGoal
    }
    fun setdistanceGoal(distanceGoal:Long){
        this.distanceGoal = distanceGoal
    }

    fun getSex():String{
        return gender
    }
    fun getWeight():Int{
        return weight
    }
    fun getHeight():Int{
        return height
    }
    fun getId():Long{
        return id
    }
    fun setId(id:Long){
        this.id = id
    }
    fun getUsername():String{
        return userName
    }
    fun setUsername(username:String){
        this.userName = username
    }
    fun getPassword():String{
        return passWord
    }
    fun setPassword(passWord:String){
        this.passWord = passWord
    }
    fun getFullname():String{
        return fullName
    }
    fun setFullname(fullName:String){
        this.fullName = fullName
    }
    fun getAvartar():String{
        return avartar
    }
    fun setAvartar(avartar:String){
        this.avartar = avartar
    }
}