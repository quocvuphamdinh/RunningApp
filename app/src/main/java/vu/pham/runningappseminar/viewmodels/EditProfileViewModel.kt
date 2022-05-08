package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.repositories.MainRepository
import java.lang.Exception

class EditProfileViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private var _errEvent: MutableLiveData<String> = MutableLiveData<String>()
    val errEvent: LiveData<String>
        get() = _errEvent

    private var _successEdit : MutableLiveData<Boolean> = MutableLiveData()
    val successEdit : LiveData<Boolean>
    get() = _successEdit

    private fun writePersonalDataToSharedPref(user: User){
        mainRepository.writePersonalDataToSharedPref(user)
    }

    fun checkInfoUser(username: String, fullname:String, height:String, weight:String):Boolean{
        val usernameValid = checkInfo(username)
        val fullnameValid = checkInfo(fullname)
        val heightValid = checkInfo(height)
        val weightValid = checkInfo(weight)
        return usernameValid && fullnameValid && heightValid && weightValid
    }

    private fun checkInfo(value: String):Boolean{
        if(value.isEmpty()){
            return false
        }
        return true
    }

    fun updateUser(user: User) = viewModelScope.launch {
        try {
            mainRepository.updateUser(user)
            writePersonalDataToSharedPref(user)
            _successEdit.postValue(true)
        }catch (e:Exception){
            _successEdit.postValue(false)
            _errEvent.postValue("An error has occurred, something happens in server !")
        }
    }
}