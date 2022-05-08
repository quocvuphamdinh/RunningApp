package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.repositories.MainRepository

class ForgotPasswordViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private var _successResetPassword : MutableLiveData<Boolean> = MutableLiveData()
    val successResetPassword : LiveData<Boolean>
    get() = _successResetPassword

    private var _toastEvent : MutableLiveData<String> = MutableLiveData()
    val toastEvent : LiveData<String>
    get() = _toastEvent

    fun resetPassword(userName: String) = viewModelScope.launch {
        try {
            val userValid = checkEmailAccount(userName)
            if(userValid.getUsername().isNotEmpty()){
                val message = mainRepository.resetPassword(userValid)
                _toastEvent.postValue(message["message"])
                _successResetPassword.postValue(true)
            }else{
                delay(1000)
                _toastEvent.postValue("Account invalid")
                _successResetPassword.postValue(false)
            }
        }catch (e:Exception){
            _toastEvent.postValue("An error has occurred, something happens in server !")
            _successResetPassword.postValue(false)
        }
    }

    private suspend fun checkEmailAccount(userName:String) = mainRepository.checkEmailAccount(userName)

    fun checkEmailIsValid(email:String):Boolean{
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return false
        }
        return true
    }
}