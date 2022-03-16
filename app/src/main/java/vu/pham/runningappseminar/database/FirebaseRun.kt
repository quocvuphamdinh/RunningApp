package vu.pham.runningappseminar.database

import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import vu.pham.runningappseminar.model.User


interface FirebaseRun {

    @GET("user/user{username}-{password}.json")
    fun getUser(@Path("username") username:String?, @Path("password") password:String?): Call<User>

    @PUT("user/user{username}-{password}.json")
    suspend fun insertUser(@Path("username") username:String?, @Path("password") password:String?, @Body user: User?)
}