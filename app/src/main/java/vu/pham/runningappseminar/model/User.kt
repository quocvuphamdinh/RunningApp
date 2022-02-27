package vu.pham.runningappseminar.model

class User() {
    private var id:Long=0L
    private var username:String=""
    private var password:String=""
    private var fullname:String=""
    private var avartar:String=""

    constructor(username: String, password: String, fullname: String, avartar: String) : this() {
        this.username = username
        this.password = password
        this.fullname = fullname
        this.avartar = avartar
    }

    fun getId():Long{
        return id
    }
    fun setId(id:Long){
        this.id = id
    }
    fun getUsername():String{
        return username
    }
    fun setUsername(username:String){
        this.username = username
    }
    fun getPassword():String{
        return password
    }
    fun setPassword(password:String){
        this.password = password
    }
    fun getFullname():String{
        return fullname
    }
    fun setFullname(fullname:String){
        this.fullname = fullname
    }
    fun getAvartar():String{
        return avartar
    }
    fun setAvartar(avartar:String){
        this.avartar = avartar
    }
}