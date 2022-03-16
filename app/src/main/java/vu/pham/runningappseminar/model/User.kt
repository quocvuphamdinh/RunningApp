package vu.pham.runningappseminar.model

class User() {
    //private var id:Long=0L
    private var username:String=""
    private var password:String=""
    private var fullname:String=""
    private var avartar:String=""
    private var sex:String=""
    private var weight:Int=0
    private var height:Int=0

    constructor(username: String, password: String, fullname: String, sex :String, weight:Int, height:Int) : this() {
        this.username = username
        this.password = password
        this.fullname = fullname
        this.sex = sex
        this.weight = weight
        this.height = height
    }

    fun getSex():String{
        return sex
    }
    fun getWeight():Int{
        return weight
    }
    fun getHeight():Int{
        return height
    }
//    fun getId():Long{
//        return id
//    }
//    fun setId(id:Long){
//        this.id = id
//    }
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