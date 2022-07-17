package vu.pham.runningappseminar.viewmodels

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.repositories.MainRepository

class VerificationViewModel(private val mainRepository: MainRepository) : ViewModel() {
    private var _toast: MutableLiveData<String> = MutableLiveData()
    val toast: LiveData<String>
        get() = _toast

    private var _success: MutableLiveData<Boolean> = MutableLiveData()
    val success: LiveData<Boolean>
        get() = _success

    fun checkEmailExists(email: String) = viewModelScope.launch {
        val message = mainRepository.checkEmailExists(email)["message"]
        _toast.postValue(message!!)
    }

    fun checkOTPCode(otpCode: String, user: User) = viewModelScope.launch {
        try {
            val message = mainRepository.checkOTPCode(otpCode)["message"]
            if (message.equals("OK")) {
                mainRepository.insertUser(user)
                checkUser(user)
            } else {
                _toast.postValue("OTP Code is not correct")
                _success.postValue(false)
            }
        } catch (e: Exception) {
            _toast.postValue(e.message.toString())
            _success.postValue(false)
        }
    }

    private fun checkUser(user: User) {
        mainRepository.getUserLogin(user.getUsername(), user.getPassword())
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    val userResult = response.body()
                    if (userResult?.getUsername() == user.getUsername() && userResult.getPassword() == user.getPassword()) {
                        _toast.postValue("Sign up success !")
                        _success.postValue(true)
                    } else {
                        _toast.postValue("Sign up failed !")
                        _success.postValue(false)
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    _toast.postValue("Error: $t !")
                    _success.postValue(false)
                }
            })
    }

    suspend fun insertUser(user: User) = mainRepository.insertUser(user)
}