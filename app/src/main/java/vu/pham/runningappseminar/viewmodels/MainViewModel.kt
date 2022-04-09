package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.database.local.Run
import vu.pham.runningappseminar.models.Activity
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.repositories.MainRepository

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private var _listActivityRun:MutableLiveData<List<Activity>> = MutableLiveData()
    val listActivityRun : LiveData<List<Activity>>
    get() = _listActivityRun
    fun getListActivityRun() = viewModelScope.launch {
        _listActivityRun.postValue(mainRepository.getListActivityByType(1))
    }

    private var _listActivityWalk:MutableLiveData<List<Activity>> = MutableLiveData()
    val listActivityWalk : LiveData<List<Activity>>
        get() = _listActivityWalk
    fun getListActivityWalk() = viewModelScope.launch {
        _listActivityWalk.postValue(mainRepository.getListActivityByType(0))
    }

    fun getFirebaseStorage() = mainRepository.getFirebaseStorage()

    fun deleteAllRun() = viewModelScope.launch {
        mainRepository.deleteAllRun()
    }

    fun insertRunRemote(run: Run, userId:Long, userActivitesId:Long) = viewModelScope.launch {
        mainRepository.insertRunRemote(run, userId, userActivitesId)
    }

    fun updateUser(user: User) = viewModelScope.launch {
        mainRepository.updateUser(user)
    }

    fun writePersonalDataToSharedPref(user: User){
        mainRepository.writePersonalDataToSharedPref(user)
    }

    fun removePersonalDataFromSharedPref(){
        mainRepository.removePersonalDataFromSharedPref()
    }

    fun getUserFromSharedPref() = mainRepository.getUserFromSharedPref()

    private var _userLiveData = MutableLiveData<User>()
    val userLiveData : LiveData<User>
    get() = _userLiveData
    fun getUserLiveData(username: String, password: String){
        viewModelScope.launch {
            _userLiveData.postValue(mainRepository.getUserLiveData(username, password))
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