package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.ViewModel
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.repositories.MainRepository

class SignUpViewModel(private val mainRepository: MainRepository) : ViewModel() {

    suspend fun checkEmailExists(email: String) = mainRepository.checkEmailExists(email)

    suspend fun checkEmailAccount(userName:String) = mainRepository.checkEmailAccount(userName)

    fun checkSameUser(username:String, password:String, user: User?):Boolean{
        if(user?.getUsername()== username && user.getPassword()==password){
            return true
        }
        return false
    }

    fun getUser(username:String, password:String) = mainRepository.getUserLogin(username, password)

    fun checkInfoUser(username: String, password: String, password2: String, fullname:String, height:String, weight:String):Boolean{
        val usernameValid = checkInfo(username) && checkEmailIsValid(username)
        val passwordValid = checkPassword(password)
        val password2Valid = checkPassword(password2)
        val fullnameValid = checkInfo(fullname)
        val heightValid = checkInfo(height)
        val weightValid = checkInfo(weight)
        return usernameValid && passwordValid && password2Valid && fullnameValid && heightValid && weightValid
    }

    private fun checkEmailIsValid(email:String):Boolean{
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return false
        }
        return true
    }

    private fun checkInfo(value: String):Boolean{
        if(value.isEmpty()){
            return false
        }
        return true
    }

    private fun checkPassword(password: String):Boolean{
        if(password.isEmpty() || password.length < 5){
            return false
        }
        return true
    }
    fun checkSamePassword(password: String, password2:String):Boolean {
        if (password != password2)
            return false
        return true
    }
}