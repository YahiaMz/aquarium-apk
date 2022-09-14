package com.aplication.aquaruim.views.customViews

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.aplication.aquaruim.R
import com.aplication.aquaruim.databinding.FailToastBinding
import com.aplication.aquaruim.databinding.SuccessToastBinding

class FailToast {
    companion object {

        public  fun showFailToast(message : String , context: Context) {
            val mToast = android.widget.Toast(context);
            val layoutInflater : LayoutInflater = LayoutInflater.from(context);
            val failToastBinding : FailToastBinding = DataBindingUtil.inflate(layoutInflater , R.layout.fail_toast , null, false)
            failToastBinding.errorMessage = message;

            mToast.view = failToastBinding.root;
            mToast.duration = Toast.LENGTH_LONG
            mToast.setGravity(Gravity.BOTTOM , 0 , 150 );
            mToast.show();

        }

    }
}