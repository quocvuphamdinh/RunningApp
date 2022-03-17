package vu.pham.runningappseminar.repositories

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vu.pham.runningappseminar.database.FirebaseRun
import vu.pham.runningappseminar.database.RetrofitBuilder
import vu.pham.runningappseminar.database.Run
import vu.pham.runningappseminar.database.RunDAO
import vu.pham.runningappseminar.model.User
import vu.pham.runningappseminar.utils.Constants

class MainRepository(private val runDAO: RunDAO, private val firebaseRun:FirebaseRun, private val sharedPref:SharedPreferences) {

    fun writePersonalDataToSharedPref(user: User){
        val gson = Gson()
        val json = gson.toJson(user)
        sharedPref.edit()
            .putString(Constants.KEY_USER, json)
            .putBoolean(Constants.KEY_FIRST_TIME_TOGGLE, false)
            .apply()
    }
    fun getUserFromSharedPref() :User?{
        val gson = Gson()
        val json = sharedPref.getString(Constants.KEY_USER, "")
        return gson.fromJson(json, User::class.java)
    }
    fun getFirstTimeToogle():Boolean{
        return sharedPref.getBoolean(Constants.KEY_FIRST_TIME_TOGGLE, true)
    }

    suspend fun insertUser(user: User){
        firebaseRun.insertUser(user.getUsername(), user.getPassword(), user)
    }

    fun getUser(username:String, password:String) = firebaseRun.getUser(username, password)


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

    fun getTotalDitanceWeekly() = runDAO.getTotalDitanceWeekly()

    fun getTotalCaloriesBurnedToDay() = runDAO.getTotalCaloriesBurnedToDay()

    fun getTotalTimeInMillisToday() = runDAO.getTotalTimeInMillisToday()

    fun getTotalAvgSpeedInKMHToday() = runDAO.getTotalAvgSpeedInKMHToday()

    fun getCountRunToday() = runDAO.getCountRunToday()

    fun getMaxDistance() = runDAO.getMaxDistance()

    fun getMaxTimeInMillies() = runDAO.getMaxTimeInMillies()

    fun getMaxCaloriesBurned() = runDAO.getMaxCaloriesBurned()

    fun getMaxAvgSpeedInKMH() = runDAO.getMaxAvgSpeedInKMH()
}