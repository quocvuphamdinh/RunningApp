package vu.pham.runningappseminar.models

import android.os.Parcel
import android.os.Parcelable

class Workout() : Parcelable{
    private var id:Long=0L
    private var name:String=""
    private var duration:Long=0

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        name = parcel.readString()!!
        duration = parcel.readLong()
    }

    constructor(name: String, duration: Long) : this() {
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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeLong(duration)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Workout> {
        override fun createFromParcel(parcel: Parcel): Workout {
            return Workout(parcel)
        }

        override fun newArray(size: Int): Array<Workout?> {
            return arrayOfNulls(size)
        }
    }
}
