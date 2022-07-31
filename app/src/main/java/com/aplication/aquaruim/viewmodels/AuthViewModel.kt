package com.aplication.aquaruim.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.aplication.aquaruim.repositories.AuthRepository

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val aContext : Context;
    private val authRepository : AuthRepository;
    init {
        this.aContext=application.applicationContext
        this.authRepository = AuthRepository.getInstance(aContext);
    }

   fun login(phoneNumber: String , password : String) : MutableLiveData<Boolean> {
return  this.authRepository.login(phoneNumber , password);
   }




}