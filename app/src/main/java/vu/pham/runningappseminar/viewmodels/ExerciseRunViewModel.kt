package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vu.pham.runningappseminar.models.Workout
import vu.pham.runningappseminar.repositories.MainRepository
import vu.pham.runningappseminar.services.Polyline

class ExerciseRunViewModel(private val mainRepository: MainRepository) : ViewModel() {

    var currentTimeInMillies=0L

    var isTracking = false
    var pathPoints = mutableListOf<Polyline>()

    var weight:Float = 80f
    var distanceInMeters = 0
    var averageSpeed = 0F
    var caloriesBurned = 0
    var workours = ArrayList<Workout>()
    var lastCurrentTime = 0L
    private var _index :MutableLiveData<Int> = MutableLiveData<Int>(0)
    val index : LiveData<Int>
    get() = _index

    fun updateIndex(){
        _index.postValue(_index.value?.plus(1) ?: 0)
    }
    fun resetData(){
        currentTimeInMillies = 0L
        distanceInMeters = 0
        averageSpeed = 0F
        caloriesBurned = 0
        lastCurrentTime = 0L
        _index.postValue(0)
    }
}