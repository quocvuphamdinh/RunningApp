package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.ViewModel
import vu.pham.runningappseminar.repositories.MainRepository
import vu.pham.runningappseminar.services.Polyline

class ExerciseRunViewModel(private val mainRepository: MainRepository) : ViewModel() {

    var currentTimeInMillies=0L

    var isTracking = false
    var pathPoints = mutableListOf<Polyline>()

    var weight:Float = 80f
    var distanceInMeters = 0
    var averageSpeed = 0F
    var caloriesBurned = 0
    var durationExercise = LongArray(3)
}