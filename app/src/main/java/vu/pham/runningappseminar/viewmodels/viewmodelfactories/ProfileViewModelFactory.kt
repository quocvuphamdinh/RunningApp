package vu.pham.runningappseminar.viewmodels.viewmodelfactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vu.pham.runningappseminar.repositories.MainRepository
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.ProfileViewModel

// class dùng để giúp cho MainViewModel có thể truyền tham số mainRepository vào dc
class ProfileViewModelFactory(private val mainRepository: MainRepository, private val app:RunApplication) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(mainRepository, app) as T
        }
        throw IllegalArgumentException("Unknown ProfileViewModel class")
    }
}