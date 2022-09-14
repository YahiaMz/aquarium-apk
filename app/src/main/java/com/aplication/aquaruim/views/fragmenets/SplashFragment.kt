package com.aplication.aquaruim.views.fragmenets

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aplication.aquaruim.R
import com.aplication.aquaruim.views.activities.MainActivity

class SplashFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {


        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onStart() {
        val sharedPreferences: SharedPreferences =
            requireContext().getSharedPreferences("USER", Context.MODE_PRIVATE);

        val isLogin = sharedPreferences.getBoolean("is_login", false);
        val userName = sharedPreferences.getString("name", "null")

        Handler(Looper.getMainLooper()).postDelayed(
            {
                if (isLogin) {
                    if (userName == "null") activity!!.supportFragmentManager.beginTransaction().replace(R.id.authFrameLayout , CompleteRegisterFragment()).commit();
                    else
                        Intent(context, MainActivity::class.java).also {
                            startActivity(it);
                        }
                } else
                    activity!!.supportFragmentManager.beginTransaction()
                        .replace(R.id.authFrameLayout, LandingFragment()).commit();
            },
            2000 // value in milliseconds
        )
        super.onStart()
    }

}