package vu.pham.runningappseminar.viewmodels

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vu.pham.runningappseminar.database.local.Run
import vu.pham.runningappseminar.model.User
import vu.pham.runningappseminar.repositories.MainRepository
import vu.pham.runningappseminar.utils.SortType
import java.util.*

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {


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

    var userLiveData = MutableLiveData<User?>()
    fun getUserLiveData(username: String, password: String){
        mainRepository.getUser(username, password)?.enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                userLiveData.value = response.body()
                Log.d("call api", response.body().toString())
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
                Log.d("call api error", t.toString())
            }
        })
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