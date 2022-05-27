package vu.pham.runningappseminar.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.repositories.MainRepository

class HomeActivityViewModel(private val mainRepository: MainRepository): ViewModel() {

    var isDisconnectedFirstTime = false

    fun syncDataRunToServer() = viewModelScope.launch {
        try {
            val listRun = mainRepository.getAllRun()
            val user = mainRepository.getUserFromSharedPref()
            for (runItem in listRun){
                mainRepository.insertRunRemote(runItem, user?.getId()!!, -1)
            }
            Log.d("home", "Sync successfully !!")
        }catch (e : Exception){
            Log.d("home", e.message.toString())
        }
    }
}