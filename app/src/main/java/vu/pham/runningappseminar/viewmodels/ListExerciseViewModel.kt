package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.models.Activity
import vu.pham.runningappseminar.repositories.MainRepository

class ListExerciseViewModel(private val mainRepository: MainRepository) : ViewModel() {
    private var _runningExercises : MutableLiveData<List<Activity>> = MutableLiveData()
    val runningExercises : LiveData<List<Activity>>
    get() = _runningExercises

    private var _walkingExercises : MutableLiveData<List<Activity>> = MutableLiveData()
    val walkingExercises : LiveData<List<Activity>>
        get() = _walkingExercises

    fun getRunningExercises() = viewModelScope.launch {
        val user = mainRepository.getUserFromSharedPref()
        _runningExercises.postValue(mainRepository.getListActivityByType(1, user?.getId()!!))
    }

    fun getWalkingExercises() = viewModelScope.launch {
        val user = mainRepository.getUserFromSharedPref()
        _walkingExercises.postValue(mainRepository.getListActivityByType(0, user?.getId()!!))
    }
}