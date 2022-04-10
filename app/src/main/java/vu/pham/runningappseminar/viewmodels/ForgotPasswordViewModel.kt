package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.ViewModel
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.repositories.MainRepository

class ForgotPasswordViewModel(private val mainRepository: MainRepository) : ViewModel() {

    suspend fun resetPassword(user: User) = mainRepository.resetPassword(user)

    suspend fun checkEmailAccount(userName:String) = mainRepository.checkEmailAccount(userName)

    fun checkEmailIsValid(email:String):Boolean{
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return false
        }
        return true
    }
}