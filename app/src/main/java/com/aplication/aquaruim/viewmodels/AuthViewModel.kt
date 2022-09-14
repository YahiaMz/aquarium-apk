package com.aplication.aquaruim.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.aplication.aquaruim.models.UpdateUserModel
import com.aplication.aquaruim.models.User
import com.aplication.aquaruim.repositories.AuthRepository

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val aContext: Context;
    private val authRepository: AuthRepository;

    init {
        this.aContext = application.applicationContext
        this.authRepository = AuthRepository.getInstance(aContext);
    }

    val sharedPreferences: SharedPreferences =
        aContext.getSharedPreferences("USER", Context.MODE_PRIVATE);
    val token = sharedPreferences.getString("token" , "no token");

    fun login(phoneNumber: String, password: String): MutableLiveData<Boolean> {
        return this.authRepository.login(phoneNumber, password);
    }



    fun signUp(phoneNumber: String, password: String): MutableLiveData<Int> {
        return this.authRepository.signUp(phoneNumber, password);
    }





    fun updateUser(updateUserModel: UpdateUserModel) : MutableLiveData<Int> {
        return  this.authRepository.updateUser(updateUserModel);
    }

}