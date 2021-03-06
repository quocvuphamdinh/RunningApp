package vu.pham.runningappseminar.database.remote

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import vu.pham.runningappseminar.models.*


interface ApiService {

    //user
    @GET("/user/{userName}-{passWord}")
    suspend fun getUserLiveData(@Path("userName") userName: String, @Path("passWord") passWord: String): User

    @GET("/user/{userName}-{passWord}")
    fun getUserLogin(@Path("userName") userName: String, @Path("passWord") passWord: String): Call<User>

    @POST("/user")
    suspend fun insertUser(@Body user: User?)

    @PUT("/user/{id}")
    suspend fun updateUser(@Body user:User, @Path("id") id:Long?)

    @GET("/user/checkemail/{userName}")
    suspend fun checkEmailAccount(@Path("userName") userName:String) : User

    @PUT("/user/resetpassword")
    suspend fun resetPassword(@Body user: User):HashMap<String, String>

    @POST("/user/checkemailexist/{email}")
    suspend fun checkEmailExists(@Path("email") email: String): HashMap<String, String>

    @POST("/user/checkotpcode/{otp}")
    suspend fun checkOTPCode(@Path("otp") otpCode: String): HashMap<String, String>

    //run
    @POST("/run/{userId}/{userActivitesId}")
    suspend fun insertRun(@Body run: Run, @Path("userId") userId: Long, @Path("userActivitesId") userActivitesId:Long)

    @GET("/run/{userId}")
    fun getRun(@Path("userId") userId:Long) : Call<List<Run>>

    //exercise
    @GET("/activity/{type}/{userId}")
    suspend fun getListActivityByType(@Path("type") type:Int, @Path("userId") userId: Long) : List<Activity>

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

    @HTTP(method = "DELETE", path = "/run/delete", hasBody = true)
    suspend fun deleteRunRemote(@Body run: Run) : Response<HashMap<String, String>>

    @DELETE("/user-activity/{userActivityId}")
    suspend fun deleteUserExercise(@Path("userActivityId") userActivityId : Long) : HashMap<String, Boolean>
}