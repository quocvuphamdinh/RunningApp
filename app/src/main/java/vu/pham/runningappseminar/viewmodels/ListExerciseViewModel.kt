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
        _runningExercises.postValue(mainRepository.getListActivityByType(1))
    }

    fun getWalkingExercises() = viewModelScope.launch {
        _walkingExercises.postValue(mainRepository.getListActivityByType(0))
    }
}