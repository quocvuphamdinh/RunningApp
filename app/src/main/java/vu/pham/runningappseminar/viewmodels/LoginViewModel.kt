package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.ViewModel
import vu.pham.runningappseminar.model.User
import vu.pham.runningappseminar.repositories.MainRepository

class LoginViewModel(private val mainRepository: MainRepository) : ViewModel() {
    fun getUser(username:String, password:String) = mainRepository.getUser(username, password)

    fun checkSameUser(username:String, password:String, user: User?):Boolean{
        if(user?.getUsername()== username && user.getPassword()==password){
            return true
        }
        return false
    }

    fun writePersonalDataToSharedPref(user: User){
        mainRepository.writePersonalDataToSharedPref(user)
    }
}