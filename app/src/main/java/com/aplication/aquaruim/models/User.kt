package com.aplication.aquaruim.models


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep data class User(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("imageProfileUrl")
    val imageProfileUrl: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("lastName")
    val lastName: String?,
    @SerializedName("password")
    val password: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("updated_at")
    val updatedAt: String

) {
}