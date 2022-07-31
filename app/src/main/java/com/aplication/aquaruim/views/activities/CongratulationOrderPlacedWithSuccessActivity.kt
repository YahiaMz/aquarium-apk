package com.aplication.aquaruim.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.aplication.aquaruim.R
import com.aplication.aquaruim.databinding.ActivityCongratulationOrderPlacedWithSuccessBinding

class CongratulationOrderPlacedWithSuccessActivity : AppCompatActivity() {

    lateinit var activityCongratulationOrderPlacedWithSuccessBinding : ActivityCongratulationOrderPlacedWithSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.activityCongratulationOrderPlacedWithSuccessBinding = DataBindingUtil.setContentView(this , R.layout.activity_congratulation_order_placed_with_success);

    }
    override fun onBackPressed() {
              super.onBackPressed()
           }


}