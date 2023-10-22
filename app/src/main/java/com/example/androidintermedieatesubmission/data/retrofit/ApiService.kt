package com.bangkit.navigationsubmission.data.retrofit


import com.example.androidintermedieatesubmission.data.response.AuthResponse
import com.example.androidintermedieatesubmission.data.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("v1/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<AuthResponse>


    @FormUrlEncoded
    @POST("v1/register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<AuthResponse>


    @GET("v1/stories")
    fun getStories(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null,

        ) : Call<StoryResponse>

    @GET("v1/stories")
    suspend fun getStoriesPaging(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null,

        ) : StoryResponse

    @Multipart
    @POST("v1/stories")
    fun addStories(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null,
    ) : Call<StoryResponse>

    @GET("v1/stories/{id}")
    fun getStory(
        @Path("id") userId: String
        ) : Call<StoryResponse>

}