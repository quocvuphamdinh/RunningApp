package vu.pham.runningappseminar.viewmodels.viewmodelfactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vu.pham.runningappseminar.repositories.MainRepository
import vu.pham.runningappseminar.viewmodels.LoginViewModel
import vu.pham.runningappseminar.viewmodels.MainViewModel
import vu.pham.runningappseminar.viewmodels.RunViewModel
import vu.pham.runningappseminar.viewmodels.WelcomeViewModel

// class dùng để giúp cho MainViewModel có thể truyền tham số mainRepository vào dc
class LoginViewModelFactory(private val mainRepository: MainRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(mainRepository) as T
        }
        throw IllegalArgumentException("Unknown LoginViewModel class")
    }
}