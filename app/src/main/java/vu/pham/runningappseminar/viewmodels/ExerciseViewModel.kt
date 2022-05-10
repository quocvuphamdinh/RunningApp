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
import vu.pham.runningappseminar.repositories.MainRepository
import vu.pham.runningappseminar.utils.RunApplication

class ExerciseViewModel(private val mainRepository: MainRepository, private val app : RunApplication) : AndroidViewModel(app) {

    private var _errEvent: MutableLiveData<String> = MutableLiveData<String>()
    val errEvent: LiveData<String>
        get() = _errEvent

    private var _listActivityRun: MutableLiveData<List<Activity>> = MutableLiveData()
    val listActivityRun : LiveData<List<Activity>>
        get() = _listActivityRun

    private var _listActivityWalk: MutableLiveData<List<Activity>> = MutableLiveData()
    val listActivityWalk : LiveData<List<Activity>>
        get() = _listActivityWalk

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
                val user = mainRepository.getUserFromSharedPref()
                val result = mainRepository.getListActivityByType(1, user?.getId()!!)
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
                val user = mainRepository.getUserFromSharedPref()
                _listActivityWalk.postValue(mainRepository.getListActivityByType(0, user?.getId()!!))
            }else{
                _errEvent.postValue("Your device does not have internet !")
            }
        }catch (e : Exception){
            _errEvent.postValue("An error has occurred, something happens in server !")
        }
    }
}