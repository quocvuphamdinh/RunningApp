package vu.pham.runningappseminar.viewmodels

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.models.*
import vu.pham.runningappseminar.repositories.MainRepository
import vu.pham.runningappseminar.utils.RunApplication

class MainViewModel(private val mainRepository: MainRepository, private val app:RunApplication) : AndroidViewModel(app) {

    private var _errEvent: MutableLiveData<String> = MutableLiveData<String>()
    val errEvent: LiveData<String>
        get() = _errEvent

    private var _listActivityRun:MutableLiveData<List<Activity>> = MutableLiveData()
    val listActivityRun : LiveData<List<Activity>>
        get() = _listActivityRun

    private var _listActivityWalk:MutableLiveData<List<Activity>> = MutableLiveData()
    val listActivityWalk : LiveData<List<Activity>>
        get() = _listActivityWalk

    private var _userLiveData = MutableLiveData<User>()
    val userLiveData : LiveData<User>
        get() = _userLiveData

    private var _recentExercise : MutableLiveData<List<UserActivityDetail>> = MutableLiveData()
    val recentExercise : LiveData<List<UserActivityDetail>>
        get() = _recentExercise

    fun getListUserExercise(userId: Long) = viewModelScope.launch {
        try{
            if(hasInternetConnection()){
                _recentExercise.postValue(mainRepository.getListUserExercise(userId))
            }else{
                _errEvent.postValue("Your device does not have internet !")
            }
        }catch (e : Exception){
            _errEvent.postValue("An error has occurred, something happens in server !")
        }
    }

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

    fun getListActivityRun() = viewModelScope.launch {
        try{
            if(hasInternetConnection()){
                val result = mainRepository.getListActivityByType(1)
                _listActivityRun.postValue(result)
            }else{
                _errEvent.postValue("Your device does not have internet !")
            }
        }catch (e : Exception){
            _errEvent.postValue("An error has occurred, something happens in server !")
        }
    }

    fun getListActivityWalk() = viewModelScope.launch {
        try{
            if(hasInternetConnection()){
                _listActivityWalk.postValue(mainRepository.getListActivityByType(0))
            }else{
                _errEvent.postValue("Your device does not have internet !")
            }
        }catch (e : Exception){
            _errEvent.postValue("An error has occurred, something happens in server !")
        }
    }

    fun getFirebaseStorage() = mainRepository.getFirebaseStorage()

    fun deleteAllRun() = viewModelScope.launch {
        mainRepository.deleteAllRun()
    }

    fun insertRunRemote(run: Run ,userId:Long, userActivitesId:Long) = viewModelScope.launch {
        try{
            if(hasInternetConnection()){
                mainRepository.insertRunRemote(run, userId, userActivitesId)
            }else{
                _errEvent.postValue("Your device does not have internet !")
            }
        }catch (e : Exception){
            _errEvent.postValue("An error has occurred, something happens in server !")
        }
    }

    fun updateUser(user: User) = viewModelScope.launch {
        try{
            if(hasInternetConnection()){
                mainRepository.updateUser(user)
                writePersonalDataToSharedPref(user)
            }else{
                _errEvent.postValue("Your device does not have internet !")
            }
        }catch (e : Exception){
            _errEvent.postValue("An error has occurred, something happens in server !")
        }
    }

    private fun writePersonalDataToSharedPref(user: User){
        mainRepository.writePersonalDataToSharedPref(user)
    }

    fun removePersonalDataFromSharedPref(){
        mainRepository.removePersonalDataFromSharedPref()
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

    suspend fun getAllRunFromLocal() = mainRepository.getAllRun()

    fun getListDistanceInSpecificDate(date:String) = mainRepository.getListDistanceInSpecificDate(date)

    val totalDistanceWeekly = mainRepository.getTotalDitanceWeekly()

    val totalCaloriesBurnedToday = mainRepository.getTotalCaloriesBurnedToDay()

    val totalTimeInMilliesToday = mainRepository.getTotalTimeInMillisToday()

    val totalAvgSpeedToday = mainRepository.getTotalAvgSpeedInKMHToday()

    val runCount = mainRepository.getCountRunToday()

    val maxDistance = mainRepository.getMaxDistance()

    val maxTimeInMillies = mainRepository.getMaxTimeInMillies()

    val maxCaloriesBurned = mainRepository.getMaxCaloriesBurned()

    val maxAvgSpeed = mainRepository.getMaxAvgSpeedInKMH()

    val totalDistance = mainRepository.getTotalDistance()

    val totalTimeInMillies = mainRepository.getTotalTimeInMillies()

    val totalCaloriesBurned = mainRepository.getTotalCaloriesBurned()

    val totalAvgSpeed = mainRepository.getTotalAvgSpeedInKMH()
}