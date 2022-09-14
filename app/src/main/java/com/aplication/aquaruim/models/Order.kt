package com.aplication.aquaruim.models

import com.aplication.aquaruim.adapters.OrderItemAdapter


data class Order(
    val id: Int,
    val zone: String,
    var status: Int,
    val orderItemsAdapter: OrderItemAdapter,
    val address: String,
    val totalPrice: Int,
    val orderTime: String,
    val phoneNumber: String,
    var isReceived : Boolean
)

