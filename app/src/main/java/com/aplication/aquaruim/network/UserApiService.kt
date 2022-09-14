package com.aplication.aquaruim.network

import com.aplication.aquaruim.network.ApiRepsonses.UpdateUserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UserApiService {


    @Multipart
    @PATCH("/users/{user_Id}")
    fun updateUser(
        @Header("Authorization" ) bToken : String ,
        @Path("user_Id") userId: String?,
        @Part("name") name: RequestBody?,
        @Part("lastName") lastName: RequestBody?,
        @Part("phoneNumber") phoneNumber: RequestBody?,
        @Part("password") password: RequestBody?,

        @Part profileImagePart: MultipartBody.Part?,
    ): Call<UpdateUserResponse>


}