package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.models.UserActivity
import vu.pham.runningappseminar.models.UserActivityDetail
import vu.pham.runningappseminar.repositories.MainRepository

class ResultExerciseRunViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private var _userActivityDetail : MutableLiveData<UserActivityDetail> = MutableLiveData()
    val userActivityDetail : LiveData<UserActivityDetail>
    get() = _userActivityDetail

    private var _toast : MutableLiveData<String> = MutableLiveData()
    val toast : LiveData<String>
    get() = _toast

    private var _success : MutableLiveData<Boolean> = MutableLiveData()
    val success : LiveData<Boolean>
    get() = _success

    private var _userFeel : MutableLiveData<Int> = MutableLiveData()
    val userFeel : LiveData<Int>
    get() = _userFeel

    fun setUserFeel(value : Int){
        _userFeel.postValue(value)
    }

    fun deleteUserExercise(userActivityId: Long) = viewModelScope.launch {
        try {
            val result = mainRepository.deleteUserExercise(userActivityId)
            val success = result["success"]!!
            if(success){
                _toast.postValue("Delete exercise successfully !!")
                _success.postValue(true)
            }else{
                _toast.postValue("Delete exercise failed !!")
                _success.postValue(false)
            }
        }catch (e:Exception){
            _toast.postValue("An error has occurred !")
            _success.postValue(false)
        }
    }

    fun getUserActivityDetail(userActivityId : Long) = viewModelScope.launch {
        try {
            _userActivityDetail.postValue(mainRepository.getUserExerciseDetail(userActivityId))
        }catch (e:Exception){
            _toast.postValue("An error has occurred !")
        }
    }

    fun updateResultRunExercise(userActivity: UserActivity, userId : Long) = viewModelScope.launch {
        try {
            mainRepository.updateUserExercise(userActivity, userId)
            _success.postValue(true)
        }catch (e:Exception){
            _success.postValue(false)
            _toast.postValue("An error has occurred !")
        }
    }
    fun getUserLocal() = mainRepository.getUserFromSharedPref()
}