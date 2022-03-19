package vu.pham.runningappseminar.database.remote

import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import vu.pham.runningappseminar.model.User


interface ApiService {

//    @GET("user/user{username}-{password}.json")
//    fun getUser(@Path("username") username:String?, @Path("password") password:String?): Call<User>

//    @PUT("user/user{username}-{password}.json")
//    suspend fun insertUser(@Path("username") username:String?, @Path("password") password:String?, @Body user: User?)

    @GET("user/{userName}-{passWord}")
    fun getUser(@Path("userName") username:String?, @Path("passWord") password:String?): Call<User?>?

    @POST("user")
    suspend fun insertUser(@Body user: User?)

    @PUT("user/{id}")
    suspend fun updateUser(@Body user:User, @Path("id") id:Long?)
}