package com.aplication.aquaruim.views.fragmenets

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
import com.aplication.aquaruim.databinding.FragmentSignUpBinding
import com.aplication.aquaruim.viewmodels.AuthViewModel
import java.util.regex.Pattern

class SignUpFragment : Fragment() {

    var mPassword_Visible = false
    lateinit var signUpBinding: FragmentSignUpBinding;
    val authViewModel: AuthViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        this.signUpBinding = FragmentSignUpBinding.inflate(layoutInflater);
        onRegisterButtonClicked()
        showHidePassword()
        this.signUpBinding.backFromSigUp.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.authFrameLayout, LandingFragment()).commit()
        }
        return this.signUpBinding.root;
    }

    private fun onRegisterButtonClicked() {
        this.signUpBinding.RegisterButton.setOnClickListener {
            val password = this.signUpBinding.SignUpPasswordEt.text.toString();
            val phoneNumber = this.signUpBinding.SignUpPhoneNumberEt.text.toString()
            val confirmedPassword: String = this.signUpBinding.confirmPassword.text.toString()



            if (validatePhoneNumber() && validatePassword(password) && isPasswordLikeConfirmPassword(
                    password,
                    confirmedPassword)
            ) {
                this.signUpBinding.isSigningUp = true;

                this.authViewModel.signUp(phoneNumber, password).observe(viewLifecycleOwner) {
                    if (it != null) {
                        if (it == 1) {
                            activity!!.supportFragmentManager.beginTransaction()
                                .replace(R.id.authFrameLayout, CompleteRegisterFragment()).commit();
                        }
                        this.signUpBinding.isSigningUp = false;
                    }

                };

            }


        }


    }

    fun showHidePassword() {
        this.signUpBinding.SignUpPasswordEt.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                val ePassword = signUpBinding.SignUpPasswordEt

                val right: Int = 2;
                if (event?.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= ePassword.getRight() - ePassword.getCompoundDrawables()[right].getBounds()
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
                return false;
            }

        });
    }

    private fun validatePhoneNumber(): Boolean {
        val phoneEditText: EditText = this.signUpBinding.SignUpPhoneNumberEt;
        val isMatch = Pattern.compile("0[5,6,7][0-9]{8}")
            .matcher(phoneEditText.text.toString())
            .matches()
        return if (isMatch) {
            true;
        } else {
            phoneEditText.error = "wrong phone number !"
            false; }
    }

    private fun validatePassword(password: String): Boolean {
        if (password.length < 4) {
            this.signUpBinding.SignUpPasswordEt.setError("Password too short")
            return false
        };
        return true
    }

    private fun isPasswordLikeConfirmPassword(
        password: String,
        confirmedPassword: String,
    ): Boolean {
        if (password != confirmedPassword) {
            this.signUpBinding.confirmPassword.setError("Those passwords didn't match")
            return false;
        }
        return true
    }

}