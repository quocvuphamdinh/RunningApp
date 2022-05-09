package vu.pham.runningappseminar.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.models.Run
import vu.pham.runningappseminar.repositories.MainRepository


class RunDetailViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private var _toastEvent : MutableLiveData<String> = MutableLiveData()
    val toastEvent : LiveData<String>
    get() = _toastEvent

    private var _successDelete : MutableLiveData<Boolean> = MutableLiveData()
    val successDelete : LiveData<Boolean>
    get() = _successDelete

    fun getFirebaseStorage() = mainRepository.getFirebaseStorage()

    fun getRunDetail(id:String) = mainRepository.getRunById(id)

    fun deleteRunWithInternet(run: Run) = viewModelScope.launch {
        try {
            val response = mainRepository.deleteRunRemote(run)
            if(response.isSuccessful){
                val result = response.body()
                val message = result!!["message"]
                if(message!!.contains("successfully")){
                    mainRepository.deleteRun(run)
                    _toastEvent.postValue(message)
                    _successDelete.postValue(true)
                }else{
                    _toastEvent.postValue(message)
                    _successDelete.postValue(false)
                }
            }else{
                _toastEvent.postValue("Delete run failed !")
                _successDelete.postValue(false)
            }
        }catch (e:Exception){
            _toastEvent.postValue("An error has occurred !")
            _successDelete.postValue(false)
            Log.d("hivu", e.message.toString())
        }
    }
}