package com.aplication.aquaruim.views.fragmenets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aplication.aquaruim.R
import com.aplication.aquaruim.databinding.FragmentLoginBinding
import com.aplication.aquaruim.viewmodels.AuthViewModel
import com.aplication.aquaruim.views.activities.MainActivity
import java.util.regex.Pattern

class LoginFragment : Fragment() {

    val authViewModel: AuthViewModel by activityViewModels();

    lateinit var fragmentLoginBinding: FragmentLoginBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        this.fragmentLoginBinding = FragmentLoginBinding.inflate(layoutInflater);
        onLoginClicked();
        goToSignUpFragmentListener();
        return fragmentLoginBinding.root;
    }


    private fun onLoginClicked() {

        this.fragmentLoginBinding.LoginButton.setOnClickListener {



            val phoneNumber = this.fragmentLoginBinding.LoginPhoneNumberEt.text.toString()
            val password = this.fragmentLoginBinding.LoginPasswordEt.text.toString();

            if (validatePhoneNumber() && validatePassword(password)) {
                this.authViewModel.login(phoneNumber, password).observe(viewLifecycleOwner) {
                    if (it != null && it) {
                        goToMainActivity();
                        activity?.finish();
                    }
                }
            }
        }
    }
    private fun goToMainActivity() {
        val toMainActivity = Intent(activity, MainActivity::class.java);
        startActivity(toMainActivity);
    }


    private fun validatePhoneNumber(): Boolean {
        val phoneEditText: EditText = this.fragmentLoginBinding.LoginPhoneNumberEt;


        val isMatch = Pattern.compile("0[5,6,7][0-9]{8}")
            .matcher(phoneEditText.text.toString())
            .matches()
        return if (isMatch) {
            true;
        } else {
            phoneEditText.error = "wrong phone number !"
            false; }
    }
    private fun validatePassword( password : String) : Boolean {
        return password.length > 6;
    }
    private fun changeVisibilityOfPassword() {

    }

    private fun goToSignUpFragmentListener( ) {
        this.fragmentLoginBinding.goToSignUpButton.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.authFrameLayout , SignUpFragment()).commit();
        }
    }
}


