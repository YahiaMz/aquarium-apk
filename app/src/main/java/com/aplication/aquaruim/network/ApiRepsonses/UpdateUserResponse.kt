package com.aplication.aquaruim.network.ApiRepsonses


import androidx.annotation.Keep
import com.aplication.aquaruim.models.User
import com.google.gson.annotations.SerializedName

@Keep data class UpdateUserResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean
)