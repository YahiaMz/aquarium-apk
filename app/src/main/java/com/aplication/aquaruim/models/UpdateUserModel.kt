package com.aplication.aquaruim.models

import okhttp3.MultipartBody
import okhttp3.RequestBody

data class UpdateUserModel(
    val   user_Id: Int,
    val name: RequestBody?,
    val lastName: RequestBody?,
    val password: RequestBody?,
    val phoneNumber: RequestBody?,
    val profileImage: MultipartBody.Part?,
)
