package vu.pham.runningappseminar.viewmodels

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.models.Activity
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.models.UserActivityDetail
import vu.pham.runningappseminar.repositories.MainRepository
import vu.pham.runningappseminar.utils.RunApplication

class HomePageViewModel(private val mainRepository: MainRepository, private val app : RunApplication) : AndroidViewModel(app) {

    private var _toastEvent: MutableLiveData<String> = MutableLiveData<String>()
    val toastEvent: LiveData<String>
        get() = _toastEvent

    private var _userLiveData = MutableLiveData<User>()
    val userLiveData : LiveData<User>
        get() = _userLiveData

    private var _recentExercise : MutableLiveData<List<UserActivityDetail>> = MutableLiveData()
    val recentExercise : LiveData<List<UserActivityDetail>>
        get() = _recentExercise

    private var _listActivityRun:MutableLiveData<List<Activity>> = MutableLiveData()
    val listActivityRun : LiveData<List<Activity>>
        get() = _listActivityRun

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<RunApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    fun clearToast() = _toastEvent.postValue("")

    fun getListActivityRun(userId: Long) = viewModelScope.launch {
        try{
            if(hasInternetConnection()){
                val result = mainRepository.getListActivityByType(1, userId)
                _listActivityRun.postValue(result)
                _toastEvent.postValue("")
            }else{
                _toastEvent.postValue("Your device does not have internet !")
            }
        }catch (e : Exception){
            _toastEvent.postValue("An error has occurred, something happens in server !")
        }
    }

    fun getListUserExercise(userId: Long) = viewModelScope.launch {
        try{
            if(hasInternetConnection()){
                _recentExercise.postValue(mainRepository.getListUserExercise(userId))
                _toastEvent.postValue("")
            }else{
                _toastEvent.postValue("Your device does not have internet !")
            }
        }catch (e : Exception){
            _toastEvent.postValue("An error has occurred, something happens in server !")
        }
    }

    fun getUserFromSharedPref() = mainRepository.getUserFromSharedPref()

    fun getUserLiveData(username: String, password: String){
        viewModelScope.launch {
            try{
                if(hasInternetConnection()){
                    _userLiveData.postValue(mainRepository.getUserLiveData(username, password))
                }else{
                    _userLiveData.postValue(getUserFromSharedPref()!!)
                }
            }catch (e : Exception){
                _userLiveData.postValue(getUserFromSharedPref()!!)
            }
        }
    }

    private fun writePersonalDataToSharedPref(user: User){
        mainRepository.writePersonalDataToSharedPref(user)
    }

    fun updateUser(user: User) = viewModelScope.launch {
        try{
            if(hasInternetConnection()){
                mainRepository.updateUser(user)
                writePersonalDataToSharedPref(user)
                _toastEvent.postValue("")
            }else{
                _toastEvent.postValue("Your device does not have internet !")
            }
        }catch (e : Exception){
            _toastEvent.postValue("An error has occurred, something happens in server !")
        }
    }

    val totalDistanceWeekly = mainRepository.getTotalDitanceWeekly()

    val totalCaloriesBurnedToday = mainRepository.getTotalCaloriesBurnedToDay()

    val totalTimeInMilliesToday = mainRepository.getTotalTimeInMillisToday()

    val totalAvgSpeedToday = mainRepository.getTotalAvgSpeedInKMHToday()

    val runCount = mainRepository.getCountRunToday()

    val maxDistance = mainRepository.getMaxDistance()

    val maxTimeInMillies = mainRepository.getMaxTimeInMillies()

    val maxCaloriesBurned = mainRepository.getMaxCaloriesBurned()

    val maxAvgSpeed = mainRepository.getMaxAvgSpeedInKMH()
}