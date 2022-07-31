package com.aplication.aquaruim.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.aplication.aquaruim.R
import com.aplication.aquaruim.databinding.ActivityAuthBinding
import com.aplication.aquaruim.viewmodels.AuthViewModel
import com.aplication.aquaruim.views.fragmenets.LoginFragment

class AuthActivity : AppCompatActivity() {
    lateinit var authActivityBinding : ActivityAuthBinding;
    lateinit var authViewModel: AuthViewModel;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.authActivityBinding = DataBindingUtil.setContentView(this , R.layout.activity_auth);
        this.authViewModel = ViewModelProvider(this)[AuthViewModel::class.java];

        val loginFragment = LoginFragment();
        val translation = supportFragmentManager.beginTransaction()
        translation.replace(R.id.authFrameLayout, loginFragment)
        translation.addToBackStack(null)
        translation.commit();

    }
}