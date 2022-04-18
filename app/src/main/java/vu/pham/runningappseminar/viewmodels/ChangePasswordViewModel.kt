package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.repositories.MainRepository

class ChangePasswordViewModel(private val mainRepository: MainRepository) : ViewModel() {


    fun writePersonalDataToSharedPref(user: User){
        mainRepository.writePersonalDataToSharedPref(user)
    }

    fun checkPassword(password: String):Boolean{
        if(password.isEmpty() || password.length < 5){
            return false
        }
        return true
    }

    fun updateUser(user: User) = viewModelScope.launch {
        mainRepository.updateUser(user)
    }

    fun checkSamePassword(password: String, password2: String):Boolean{
        if(password == password2){
            return true
        }
        return false
    }
}