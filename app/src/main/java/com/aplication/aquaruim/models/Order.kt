package com.aplication.aquaruim.models

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.aplication.aquaruim.adapters.OrderItemAdapter


data class Order( val id : Int , val status : Int , val orderItemsAdapter : OrderItemAdapter, val address :String , val totalPrice : Int , val orderTime : String , val phoneNumber: String);

