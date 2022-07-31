package com.aplication.aquaruim.views.customViews

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.aplication.aquaruim.R
import com.aplication.aquaruim.databinding.SuccessToastBinding

class SuccessToast  {

    companion object {

        public  fun ShowSuccessToast(message : String , context: Context) {
            val mToast = android.widget.Toast(context);
            val layoutInflater : LayoutInflater = LayoutInflater.from(context);
            val successToastBinding : SuccessToastBinding = DataBindingUtil.inflate(layoutInflater , R.layout.success_toast , null, false)
            successToastBinding.message = message;

            mToast.view = successToastBinding.root;
            mToast.duration =Toast.LENGTH_SHORT;
            mToast.setGravity(Gravity.BOTTOM , 0 , 150 );
            mToast.show();

        }

    }

}
