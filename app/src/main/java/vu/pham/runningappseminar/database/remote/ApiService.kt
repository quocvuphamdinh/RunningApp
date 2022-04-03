package vu.pham.runningappseminar.database.remote

import retrofit2.Call
import retrofit2.http.*
import vu.pham.runningappseminar.database.local.Run
import vu.pham.runningappseminar.model.Activity
import vu.pham.runningappseminar.model.User


interface ApiService {

    @GET("user/{userName}-{passWord}")
    fun getUser(@Path("userName") username:String?, @Path("passWord") password:String?): Call<User?>?

    @POST("user")
    suspend fun insertUser(@Body user: User?)

    @PUT("user/{id}")
    suspend fun updateUser(@Body user:User, @Path("id") id:Long?)

    @POST("run/{userId}/{userActivitesId}")
    suspend fun insertRun(@Body run: Run, @Path("userId") userId: Long, @Path("userActivitesId") userActivitesId:Long)

    @GET("run/{userId}")
    fun getRun(@Path("userId") userId:Long) : Call<List<Run>>

    @GET("activity/{type}")
    fun getListActivityByType(@Path("type") type:Int) : Call<List<Activity>>

    @GET("activity/detail/{id}")
    fun getActivityDetail(@Path("id") id:Long) : Call<Activity>
}