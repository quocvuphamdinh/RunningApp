package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.ViewModel
import vu.pham.runningappseminar.repositories.MainRepository

class WelcomeViewModel(private val mainRepository: MainRepository) : ViewModel() {
    fun getFirstTimeToogle() = mainRepository.getFirstTimeToogle()
}