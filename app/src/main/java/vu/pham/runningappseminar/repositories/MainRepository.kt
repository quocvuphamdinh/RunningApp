package vu.pham.runningappseminar.repositories

import vu.pham.runningappseminar.database.Run
import vu.pham.runningappseminar.database.RunDAO

class MainRepository(private val runDAO: RunDAO) {

    suspend fun insertRun(run: Run){
        runDAO.insertRun(run)
    }

    suspend fun deleteRun(run: Run){
        runDAO.deleteRun(run)
    }

    fun getAllRunsSortedByDate() = runDAO.getAllRunsSortedByDate()

    fun getAllRunsSortedByTimeInMillies() = runDAO.getAllRunsSortedByTimeInMillies()

    fun getAllRunsSortedByAvgSpeedInKMH() = runDAO.getAllRunsSortedByAvgSpeedInKMH()

    fun getAllRunsSortedByCaloriesBurned() = runDAO.getAllRunsSortedByCaloriesBurned()

    fun getAllRunsSortedByDistance() = runDAO.getAllRunsSortedByDistance()

    fun getTotalTimeInMillies() = runDAO.getTotalTimeInMillies()

    fun getTotalAvgSpeedInKMH() = runDAO.getTotalAvgSpeedInKMH()

    fun getTotalCaloriesBurned() = runDAO.getTotalCaloriesBurned()

    fun getTotalDistance() = runDAO.getTotalDistance()
}