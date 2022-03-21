package vu.pham.runningappseminar.viewmodels.viewmodelfactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vu.pham.runningappseminar.repositories.MainRepository
import vu.pham.runningappseminar.viewmodels.MainViewModel
import vu.pham.runningappseminar.viewmodels.RunViewModel

// class dùng để giúp cho MainViewModel có thể truyền tham số mainRepository vào dc
class RunViewModelFactory(private val mainRepository: MainRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RunViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RunViewModel(mainRepository) as T
        }
        throw IllegalArgumentException("Unknown RunViewModel class")
    }
}