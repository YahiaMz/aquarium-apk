package com.aplication.aquaruim.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.aplication.aquaruim.R
import com.aplication.aquaruim.databinding.ActivityAuthBinding
import com.aplication.aquaruim.viewmodels.AuthViewModel
import com.aplication.aquaruim.views.fragmenets.LandingFragment
import com.aplication.aquaruim.views.fragmenets.LoginFragment
import com.aplication.aquaruim.views.fragmenets.SignUpFragment
import com.aplication.aquaruim.views.fragmenets.SplashFragment
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {
    lateinit var authActivityBinding : ActivityAuthBinding;
    lateinit var authViewModel: AuthViewModel;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.authActivityBinding = DataBindingUtil.setContentView(this , R.layout.activity_auth);
        this.authViewModel = ViewModelProvider(this)[AuthViewModel::class.java];
        supportFragmentManager.beginTransaction().replace(R.id.authFrameLayout , SplashFragment()).commit();

    }
}