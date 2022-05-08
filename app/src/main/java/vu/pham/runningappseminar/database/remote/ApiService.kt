package vu.pham.runningappseminar.database.remote

import retrofit2.Call
import retrofit2.http.*
import vu.pham.runningappseminar.models.*


interface ApiService {

    //user
    @GET("/user/{userName}-{passWord}")
    suspend fun getUserLiveData(@Path("userName") username:String?, @Path("passWord") password:String?): User

    @GET("/user/{userName}-{passWord}")
    fun getUserLogin(@Path("userName") username:String?, @Path("passWord") password:String?): Call<User>

    @POST("/user")
    suspend fun insertUser(@Body user: User?)

    @PUT("/user/{id}")
    suspend fun updateUser(@Body user:User, @Path("id") id:Long?)

    @GET("/user/checkemail/{userName}")
    suspend fun checkEmailAccount(@Path("userName") userName:String) : User

    @PUT("/user/resetpassword")
    suspend fun resetPassword(@Body user: User):HashMap<String, String>

    //run
    @POST("/run/{userId}/{userActivitesId}")
    suspend fun insertRun(@Body run: Run, @Path("userId") userId: Long, @Path("userActivitesId") userActivitesId:Long)

    @GET("/run/{userId}")
    fun getRun(@Path("userId") userId:Long) : Call<List<Run>>

    //exercise
    @GET("/activity/{type}")
    suspend fun getListActivityByType(@Path("type") type:Int) : List<Activity>

    @GET("/activity/detail/{id}")
    suspend fun getActivityDetail(@Path("id") id:Long) : Activity

    //exercise user
    @POST("/user-activity/{userId}")
    suspend fun insertUserExercise(@Body userActivity: UserActivity, @Path("userId") userId:Long) : UserActivity

    @GET("/user-activity/{userId}")
    suspend fun getListUserExercise(@Path("userId") userId:Long) : List<UserActivityDetail>

    @GET("/user-activity/calculate-recent-activity/{userId}")
    suspend fun calculateDataRecentActivity(@Path("userId") userId: Long) : HashMap<String, String>

    @GET("/user-activity/detail/{userActivityId}")
    suspend fun getUserExerciseDetail(@Path("userActivityId") userActivityId:Long) : UserActivityDetail

    @PUT("/user-activity/{userId}")
    suspend fun updateUserExercise(@Body userActivity: UserActivity, @Path("userId") userId: Long) : UserActivity
}