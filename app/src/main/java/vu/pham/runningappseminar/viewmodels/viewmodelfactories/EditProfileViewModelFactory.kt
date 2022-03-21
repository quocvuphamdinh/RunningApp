package vu.pham.runningappseminar.viewmodels.viewmodelfactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vu.pham.runningappseminar.repositories.MainRepository
import vu.pham.runningappseminar.viewmodels.EditProfileViewModel

class EditProfileViewModelFactory(private val mainRepository: MainRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditProfileViewModel(mainRepository) as T
        }
        throw IllegalArgumentException("Unknown EditProfileViewModel class")
    }
}