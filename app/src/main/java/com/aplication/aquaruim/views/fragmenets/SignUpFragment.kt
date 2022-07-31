package com.aplication.aquaruim.views.fragmenets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aplication.aquaruim.R
import com.aplication.aquaruim.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {
    lateinit var signUpBinding: FragmentSignUpBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
         this.signUpBinding = FragmentSignUpBinding.inflate(layoutInflater);
        goToLoginFragmentListener();
        return this.signUpBinding.root;
    }

    private fun goToLoginFragmentListener( ){
       this.signUpBinding.goToLoginButton.setOnClickListener {
           activity!!.supportFragmentManager.beginTransaction().replace(R.id.authFrameLayout , LoginFragment()).commit();
       }
    }

}