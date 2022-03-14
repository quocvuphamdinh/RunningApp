package vu.pham.runningappseminar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface RunDAO {

    @Insert
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query("SELECT * FROM running_table ORDER BY timestamp DESC")
    fun getAllRunsSortedByDate():LiveData<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY timeInMillis DESC")
    fun getAllRunsSortedByTimeInMillies():LiveData<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY averageSpeedInKilometersPerHour DESC")
    fun getAllRunsSortedByAvgSpeedInKMH():LiveData<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY caloriesBurned DESC")
    fun getAllRunsSortedByCaloriesBurned():LiveData<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY distanceInKilometers DESC")
    fun getAllRunsSortedByDistance():LiveData<List<Run>>

    @Query("SELECT SUM(timeInMillis) FROM running_table")
    fun getTotalTimeInMillies():LiveData<Long>

    @Query("SELECT AVG(averageSpeedInKilometersPerHour) FROM running_table")
    fun getTotalAvgSpeedInKMH():LiveData<Float>

    @Query("SELECT SUM(caloriesBurned) FROM running_table")
    fun getTotalCaloriesBurned():LiveData<Int>

    @Query("SELECT SUM(distanceInKilometers) FROM running_table")
    fun getTotalDistance():LiveData<Int>

    @Query("SELECT SUM(distanceInKilometers) FROM running_table WHERE strftime('%W',DATE(DATETIME(timestamp/1000, 'unixepoch'))) = strftime('%W',DATE('now'))")
    fun getTotalDitanceWeekly():LiveData<Int>

    @Query("SELECT SUM(caloriesBurned) FROM running_table WHERE DATE(DATETIME(timestamp/1000, 'unixepoch')) = DATE('now')")
    fun getTotalCaloriesBurnedToDay():LiveData<Int>

    @Query("SELECT SUM(timeInMillis) FROM running_table WHERE DATE(DATETIME(timestamp/1000, 'unixepoch')) = DATE('now')")
    fun getTotalTimeInMillisToday():LiveData<Long>

    @Query("SELECT SUM(averageSpeedInKilometersPerHour) FROM running_table WHERE DATE(DATETIME(timestamp/1000, 'unixepoch')) = DATE('now')")
    fun getTotalAvgSpeedInKMHToday():LiveData<Float>

    @Query("SELECT COUNT(*) FROM running_table WHERE DATE(DATETIME(timestamp/1000, 'unixepoch')) = DATE('now')")
    fun getCountRunToday():LiveData<Int>

    @Query("SELECT MAX(distanceInKilometers) FROM running_table")
    fun getMaxDistance():LiveData<Int>

    @Query("SELECT MAX(timeInMillis) FROM running_table")
    fun getMaxTimeInMillies():LiveData<Long>

    @Query("SELECT MAX(caloriesBurned) FROM running_table")
    fun getMaxCaloriesBurned():LiveData<Int>

    @Query("SELECT MAX(averageSpeedInKilometersPerHour) FROM running_table")
    fun getMaxAvgSpeedInKMH():LiveData<Float>
}