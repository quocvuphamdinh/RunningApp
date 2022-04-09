package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.models.Activity
import vu.pham.runningappseminar.repositories.MainRepository

class DetailExerciseViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private var _activityDetail:MutableLiveData<Activity> = MutableLiveData()
    val activityDetail : LiveData<Activity>
    get() = _activityDetail
    fun getActivityDetail(id:Long) = viewModelScope.launch {
        _activityDetail.postValue(mainRepository.getActivityDetail(id))
    }
}