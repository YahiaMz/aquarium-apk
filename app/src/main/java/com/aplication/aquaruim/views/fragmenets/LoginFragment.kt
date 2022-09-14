package com.aplication.aquaruim.views.fragmenets

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.MotionEvent
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
    lateinit var sharedPreferences: SharedPreferences;
    var mPassword_Visible: Boolean = false
    lateinit var fragmentLoginBinding: FragmentLoginBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        this.fragmentLoginBinding = FragmentLoginBinding.inflate(layoutInflater);
        this.sharedPreferences = context!!.getSharedPreferences("USER" , Context.MODE_PRIVATE);
        this.fragmentLoginBinding.isLogging = false;
        initPhoneNumber()
        showHidePassword();
        onLoginClicked();

        this.fragmentLoginBinding.backFromSignIn.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.authFrameLayout, LandingFragment()).commit()
        }
        return fragmentLoginBinding.root;
    }

    fun initPhoneNumber() {
        val phoneNumber = this.sharedPreferences.getString("phone_number", "");
        if (phoneNumber != "") {
            fragmentLoginBinding.LoginPhoneNumberEt.setText(phoneNumber);
        }
        }

        @SuppressLint("ClickableViewAccessibility")
        fun showHidePassword() =
            this.fragmentLoginBinding.LoginPasswordEt.setOnTouchListener { p0, event ->
                val ePassword = fragmentLoginBinding.LoginPasswordEt

                val right: Int = 2;
                if (event?.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= ePassword.getRight() - fragmentLoginBinding.LoginPasswordEt.getCompoundDrawables()[right].getBounds()
                            .width()
                    ) {
                        if (mPassword_Visible) {

                            ePassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.lock_icon_selector,
                                0,
                                R.drawable.ic_visibility_off,
                                0);
                            ePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            mPassword_Visible = false;

                        } else {
                            ePassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.lock_icon_selector,
                                0,
                                R.drawable.ic_visibility_on,
                                0);
                            ePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            mPassword_Visible = true;
                        }
                    }
                }
                false;
            }

        private fun onLoginClicked() {

            this.fragmentLoginBinding.mLoginButton.setOnClickListener {


                val phoneNumber = this.fragmentLoginBinding.LoginPhoneNumberEt.text.toString()
                val password = this.fragmentLoginBinding.LoginPasswordEt.text.toString();

                if (validatePhoneNumber() && password.isNotEmpty()) {
                    this.fragmentLoginBinding.isLogging = true;
                    this.authViewModel.login(phoneNumber, password).observe(viewLifecycleOwner) {
                        if (it != null) {
                            if (it) {

                                val userName = sharedPreferences.getString("name", "null");
                                if (userName != "null") {
                                    goToMainActivity()
                                } else {
                                    activity!!.supportFragmentManager.beginTransaction()
                                        .replace(R.id.authFrameLayout, CompleteRegisterFragment())
                                        .commit();
                                }

                            }

                            this.fragmentLoginBinding.isLogging = false;
                        }
                    }
                }
            }
        }

        private fun goToMainActivity() {
            val toMainActivity = Intent(activity, MainActivity::class.java);
            startActivity(toMainActivity);
            activity?.finish()
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






    }


