package vu.pham.runningappseminar.model

class Sex {
    private var id:Long=0L
    private var name:String=""

    constructor(id: Long, name: String) {
        this.id = id
        this.name = name
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

    override fun toString(): String {
        return name
    }
}