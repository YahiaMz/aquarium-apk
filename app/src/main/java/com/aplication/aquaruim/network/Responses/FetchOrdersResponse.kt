package com.aplication.aquaruim.network.Responses

import com.aplication.aquaruim.models.Order
import com.aplication.aquaruim.utils.MResponseStatus

data class FetchOrdersResponse(val status: MResponseStatus, val orders: ArrayList<Order>?);
