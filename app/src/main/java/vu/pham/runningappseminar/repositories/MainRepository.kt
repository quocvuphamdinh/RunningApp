package vu.pham.runningappseminar.repositories

import android.content.SharedPreferences
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import vu.pham.runningappseminar.database.remote.ApiService
import vu.pham.runningappseminar.models.Run
import vu.pham.runningappseminar.database.local.RunDAO
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.models.UserActivity
import vu.pham.runningappseminar.utils.Constants

class MainRepository(
    private val runDAO: RunDAO,
    private val apiService: ApiService,
    private val sharedPref:SharedPreferences,
    private val firebaseStorageService:FirebaseStorage?
) {

    //remote
    suspend fun checkOTPCode(otpCode: String) = apiService.checkOTPCode(otpCode)

    suspend fun checkEmailExists(email: String) = apiService.checkEmailExists(email)

    suspend fun deleteUserExercise(userActivityId: Long) = apiService.deleteUserExercise(userActivityId)

    suspend fun deleteRunRemote(run:Run) = apiService.deleteRunRemote(run)

    suspend fun updateUserExercise(userActivity: UserActivity, userId: Long) = apiService.updateUserExercise(userActivity, userId)

    suspend fun getUserExerciseDetail(userActivityId: Long) = apiService.getUserExerciseDetail(userActivityId)

    suspend fun calculateDataRecentActivity(userId: Long) = apiService.calculateDataRecentActivity(userId)

    suspend fun getListUserExercise(userId: Long) = apiService.getListUserExercise(userId)

    suspend fun insertUserExercise(userActivity: UserActivity, userId: Long) = apiService.insertUserExercise(userActivity, userId)

    suspend fun resetPassword(user: User) = apiService.resetPassword(user)

    suspend fun checkEmailAccount(userName:String) = apiService.checkEmailAccount(userName)

    suspend fun getActivityDetail(id:Long) = apiService.getActivityDetail(id)

    suspend fun getListActivityByType(type:Int, userId: Long) = apiService.getListActivityByType(type, userId)

    fun getFirebaseStorage() = firebaseStorageService

    fun getAllRunFromRemote(userId: Long) = apiService.getRun(userId)

    suspend fun insertUser(user: User)= apiService.insertUser(user)

    suspend fun getUserLiveData(userName: String, passWord: String) = apiService.getUserLiveData(userName, passWord)

    fun getUserLogin(userName: String, passWord: String) = apiService.getUserLogin(userName, passWord)

    suspend fun updateUser(user: User) = apiService.updateUser(user, user.getId())

    suspend fun insertRunRemote(run: Run, userId:Long, userActivitesId:Long){
        apiService.insertRun(run, userId, userActivitesId)
    }

    //local
    fun getTotalAvgSpeedInEachMonth(date: Long) = runDAO.getTotalAvgSpeedInEachMonth(date)

    fun getTotalAvgSpeedInEachDay(date: Long) = runDAO.getTotalAvgSpeedInEachDay(date)

    fun getTotalCaloriesBurnedInEachMonth(date: Long) = runDAO.getTotalCaloriesBurnedInEachMonth(date)

    fun getTotalDurationInEachMonth(date: Long) = runDAO.getTotalDurationInEachMonth(date)

    fun getTotalDistanceInEachMonth(date: Long) = runDAO.getTotalDistanceInEachMonth(date)

    fun getTotalDistanceInEachDay(date: Long) = runDAO.getTotalDistanceInEachDay(date)

    fun getTotalDurationInEachDay(date: Long) = runDAO.getTotalDurationInEachDay(date)

    fun getTotalCaloriesBurnedInEachDay(date: Long) = runDAO.getTotalCaloriesBurnedInEachDay(date)

    fun getRunById(id:String) = runDAO.getRunById(id)

    suspend fun deleteAllRun() = runDAO.deleteAllRun()

    suspend fun getAllRun() = runDAO.getAllRun()

    fun writePersonalDataToSharedPref(user: User){
        val gson = Gson()
        val json = gson.toJson(user)
        sharedPref.edit()
            .putString(Constants.KEY_USER, json)
            .putBoolean(Constants.KEY_FIRST_TIME_TOGGLE, false)
            .apply()
    }

    fun removePersonalDataFromSharedPref(){
        sharedPref.edit()
            .remove(Constants.KEY_USER)
            .remove(Constants.KEY_FIRST_TIME_TOGGLE)
            .apply()
    }

    fun getUserFromSharedPref() : User? {
        val gson = Gson()
        val json = sharedPref.getString(Constants.KEY_USER, "")
        return gson.fromJson(json, User::class.java)
    }
    fun getFirstTimeToogle():Boolean{
        return sharedPref.getBoolean(Constants.KEY_FIRST_TIME_TOGGLE, true)
    }

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

    fun getAllRunsWithExerciseSortedByDate() = runDAO.getAllRunsWithExerciseSortedByDate()

    fun getAllRunsWithExerciseSortedByTimeInMillies() = runDAO.getAllRunsWithExerciseSortedByTimeInMillies()

    fun getAllRunsWithExerciseSortedByAvgSpeedInKMH() = runDAO.getAllRunsWithExerciseSortedByAvgSpeedInKMH()

    fun getAllRunsWithExerciseSortedByCaloriesBurned() = runDAO.getAllRunsWithExerciseSortedByCaloriesBurned()

    fun getAllRunsWithExerciseSortedByDistance() = runDAO.getAllRunsWithExerciseSortedByDistance()

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