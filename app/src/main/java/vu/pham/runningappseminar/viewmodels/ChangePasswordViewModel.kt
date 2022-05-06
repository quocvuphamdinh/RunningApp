package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.repositories.MainRepository

class ChangePasswordViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private var _errEvent: MutableLiveData<String> = MutableLiveData<String>()
    val errEvent: LiveData<String>
        get() = _errEvent

    private fun writePersonalDataToSharedPref(user: User){
        mainRepository.writePersonalDataToSharedPref(user)
    }

    fun checkPassword(password: String):Boolean{
        if(password.isEmpty() || password.length < 5){
            return false
        }
        return true
    }

    fun updateUser(user: User) = viewModelScope.launch {
        try {
            mainRepository.updateUser(user)
            writePersonalDataToSharedPref(user)
        }catch (e:Exception){
            _errEvent.postValue("An error has occurred, something happens in server !")
        }
    }

    fun checkSamePassword(password: String, password2: String):Boolean{
        if(password == password2){
            return true
        }
        return false
    }
}