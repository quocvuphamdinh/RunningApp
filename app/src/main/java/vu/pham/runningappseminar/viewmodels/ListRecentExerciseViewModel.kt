package vu.pham.runningappseminar.viewmodels

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.models.UserActivityDetail
import vu.pham.runningappseminar.repositories.MainRepository
import vu.pham.runningappseminar.utils.RunApplication

class ListRecentExerciseViewModel(private val mainRepository: MainRepository, private val app:RunApplication) : AndroidViewModel(app) {
    private var _recentExercises : MutableLiveData<List<UserActivityDetail>> = MutableLiveData(ArrayList())
    val recentExercises : LiveData<List<UserActivityDetail>>
    get() = _recentExercises

    private var _errEvent: MutableLiveData<String> = MutableLiveData<String>()
    val errEvent: LiveData<String>
        get() = _errEvent

    private var _totalDistance: MutableLiveData<Int> = MutableLiveData<Int>(0)
    val totalDistance: LiveData<Int>
        get() = _totalDistance

    private var _totalDuration: MutableLiveData<Long> = MutableLiveData<Long>(0L)
    val totalDuration: LiveData<Long>
        get() = _totalDuration

    private var _totalCaloriesBurned: MutableLiveData<Int> = MutableLiveData<Int>(0)
    val totalCaloriesBurned: LiveData<Int>
        get() = _totalCaloriesBurned

    private var _totalAvgSpeed: MutableLiveData<Float> = MutableLiveData<Float>(0F)
    val totalAvgSpeed: LiveData<Float>
        get() = _totalAvgSpeed

    fun getListRecentExercises(userId:Long) = viewModelScope.launch {
        try{
            if(hasInternetConnection()){
                _recentExercises.postValue(mainRepository.getListUserExercise(userId))
                _errEvent.postValue("")
            }else{
                _errEvent.postValue("Your device does not have internet !")
            }
        }catch (e : Exception){
            _recentExercises.postValue(ArrayList())
            _errEvent.postValue("An error has occurred, please check your internet !")
        }
    }
    fun calculateDataRecentExercise(userId: Long) = viewModelScope.launch{
        try{
            if(hasInternetConnection()){
                _totalDistance.postValue(mainRepository.calculateDataRecentActivity(userId)["distance"]!!.toInt())
                _totalDuration.postValue(mainRepository.calculateDataRecentActivity(userId)["duration"]!!.toLong())
                _totalCaloriesBurned.postValue(mainRepository.calculateDataRecentActivity(userId)["caloriesBurned"]!!.toInt())
                _totalAvgSpeed.postValue(mainRepository.calculateDataRecentActivity(userId)["avgSpeed"]!!.toFloat())
            }else{
                _errEvent.postValue("Your device does not have internet !")
            }
        }catch (e : Exception){
            _totalDistance.postValue(0)
            _totalDuration.postValue(0L)
            _totalCaloriesBurned.postValue(0)
            _totalAvgSpeed.postValue(0F)
            _errEvent.postValue("An error has occurred, please check your internet !")
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
}