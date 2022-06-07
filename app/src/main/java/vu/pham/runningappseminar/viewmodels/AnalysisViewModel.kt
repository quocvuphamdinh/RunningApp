package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.*
import vu.pham.runningappseminar.repositories.MainRepository

class AnalysisViewModel(private val mainRepository: MainRepository) : ViewModel() {

    fun getTotalAvgSpeedInEachDay(date: Long) = mainRepository.getTotalAvgSpeedInEachDay(date)

    fun getTotalCaloriesBurnedInEachDay(date: Long) = mainRepository.getTotalCaloriesBurnedInEachDay(date)

    fun getTotalDurationInEachDay(date: Long) = mainRepository.getTotalDurationInEachDay(date)

    fun getTotalDistanceInEachDay(date: Long) = mainRepository.getTotalDistanceInEachDay(date)

    fun getTotalDistanceInEachMonth(date: Long) = mainRepository.getTotalDistanceInEachMonth(date)

    fun getTotalDurationInEachMonth(date: Long) = mainRepository.getTotalDurationInEachMonth(date)

    fun getTotalCaloriesBurnedInEachMonth(date: Long) = mainRepository.getTotalCaloriesBurnedInEachMonth(date)

    fun getTotalAvgSpeedInEachMonth(date: Long) = mainRepository.getTotalAvgSpeedInEachMonth(date)
}