package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.ViewModel
import vu.pham.runningappseminar.repositories.MainRepository

class RunDetailViewModel(private val mainRepository: MainRepository) : ViewModel() {

    fun getRunDetail(id:String) = mainRepository.getRunById(id)
}