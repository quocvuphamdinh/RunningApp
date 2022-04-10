package vu.pham.runningappseminar.database.remote

import retrofit2.Call
import retrofit2.http.*
import vu.pham.runningappseminar.database.local.Run
import vu.pham.runningappseminar.models.Activity
import vu.pham.runningappseminar.models.User


interface ApiService {

    @GET("user/{userName}-{passWord}")
    suspend fun getUserLiveData(@Path("userName") username:String?, @Path("passWord") password:String?): User

    @GET("user/{userName}-{passWord}")
    fun getUserLogin(@Path("userName") username:String?, @Path("passWord") password:String?): Call<User>

    @POST("user")
    suspend fun insertUser(@Body user: User?)

    @PUT("user/{id}")
    suspend fun updateUser(@Body user:User, @Path("id") id:Long?)

    @POST("run/{userId}/{userActivitesId}")
    suspend fun insertRun(@Body run: Run, @Path("userId") userId: Long, @Path("userActivitesId") userActivitesId:Long)

    @GET("run/{userId}")
    fun getRun(@Path("userId") userId:Long) : Call<List<Run>>

    @GET("activity/{type}")
    suspend fun getListActivityByType(@Path("type") type:Int) : List<Activity>

    @GET("activity/detail/{id}")
    suspend fun getActivityDetail(@Path("id") id:Long) : Activity

    @GET("user/checkemail/{userName}")
    suspend fun checkEmailAccount(@Path("userName") userName:String) : User

    @PUT("user/resetpassword")
    suspend fun resetPassword(@Body user: User):HashMap<String, String>
}