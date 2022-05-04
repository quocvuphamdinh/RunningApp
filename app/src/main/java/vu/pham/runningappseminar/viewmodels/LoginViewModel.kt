package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.models.Run
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.repositories.MainRepository

class LoginViewModel(private val mainRepository: MainRepository) : ViewModel() {

    fun getAllRunFromRemote(userId: Long) = mainRepository.getAllRunFromRemote(userId)

    fun insertRunLocal(run: Run) = viewModelScope.launch {
        mainRepository.insertRun(run)
    }

    fun getUser(username:String, password:String) = mainRepository.getUserLogin(username, password)

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