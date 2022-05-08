package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.models.Run
import vu.pham.runningappseminar.repositories.MainRepository
import java.lang.Exception

class RunViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private var _success : MutableLiveData<Boolean> = MutableLiveData()
    val success : LiveData<Boolean>
    get() = _success

    private var _toastEvent : MutableLiveData<String> = MutableLiveData()
    val toastEvent : LiveData<String>
    get() = _toastEvent

    fun getFirebaseStorage() = mainRepository.getFirebaseStorage()

    fun insertRun(run: Run) = viewModelScope.launch {
        try {
            mainRepository.insertRun(run)
            _toastEvent.postValue("Run saved successfully !!")
            _success.postValue(true)
        }catch (e:Exception){
            _toastEvent.postValue("Run saved failed !!")
            _success.postValue(false)
        }
    }
    fun getUserFromSharedPref() = mainRepository.getUserFromSharedPref()
}