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
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.repositories.MainRepository
import vu.pham.runningappseminar.utils.RunApplication

class ProfileViewModel(
    private val mainRepository: MainRepository,
    private val app: RunApplication
) : AndroidViewModel(app) {

    private var _toastEvent: MutableLiveData<String> = MutableLiveData<String>()
    val toastEvent: LiveData<String>
        get() = _toastEvent

    private var _userLiveData = MutableLiveData<User>()
    val userLiveData: LiveData<User>
        get() = _userLiveData

    private var _success: MutableLiveData<Boolean> = MutableLiveData()
    val success: LiveData<Boolean>
        get() = _success

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<RunApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
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
    fun getFirebaseStorage() = mainRepository.getFirebaseStorage()

    fun syncData(userId: Long) = viewModelScope.launch {
        try {
            if(hasInternetConnection()){
                val listRun = getAllRunFromLocal()
                for (runItem in listRun){
                    mainRepository.insertRunRemote(runItem, userId, -1)
                }
                _toastEvent.postValue("Sync successfully !")
            }else{
                _toastEvent.postValue("Sync failed !")
            }
        }catch (e : Exception){
            _toastEvent.postValue("An error has occurred, something happens in server !")
        }
    }

    fun logOut(userId: Long) = viewModelScope.launch {
        try {
            if(hasInternetConnection()){
                val listRun = getAllRunFromLocal()
                for (run in listRun){
                    mainRepository.insertRunRemote(run, userId, -1)
                }
                mainRepository.deleteAllRun()
                mainRepository.removePersonalDataFromSharedPref()
                _success.postValue(true)
            }else{
                _toastEvent.postValue("Your device does not have internet !")
                _success.postValue(false)
            }
        }catch (e : Exception){
            _toastEvent.postValue("An error has occurred, something happens in server !")
            _success.postValue(false)
        }
    }

    fun updateUser(user: User) = viewModelScope.launch {
        try {
            if (hasInternetConnection()) {
                mainRepository.updateUser(user)
                writePersonalDataToSharedPref(user)
                _toastEvent.postValue("")
            } else {
                _toastEvent.postValue("Your device does not have internet !")
            }
        } catch (e: Exception) {
            _toastEvent.postValue("An error has occurred, something happens in server !")
        }
    }

    private fun writePersonalDataToSharedPref(user: User) {
        mainRepository.writePersonalDataToSharedPref(user)
    }

    fun getUserFromSharedPref() = mainRepository.getUserFromSharedPref()

    fun getUserLiveData(username: String, password: String) {
        viewModelScope.launch {
            try {
                if (hasInternetConnection()) {
                    _userLiveData.postValue(mainRepository.getUserLiveData(username, password))
                } else {
                    _userLiveData.postValue(getUserFromSharedPref()!!)
                }
            } catch (e: Exception) {
                val user = getUserFromSharedPref()
                if (user != null) {
                    _userLiveData.postValue(getUserFromSharedPref()!!)
                }
            }
        }
    }

    private suspend fun getAllRunFromLocal() = mainRepository.getAllRun()

    val totalDistance = mainRepository.getTotalDistance()

    val totalTimeInMillies = mainRepository.getTotalTimeInMillies()

    val totalCaloriesBurned = mainRepository.getTotalCaloriesBurned()

    val totalAvgSpeed = mainRepository.getTotalAvgSpeedInKMH()
}