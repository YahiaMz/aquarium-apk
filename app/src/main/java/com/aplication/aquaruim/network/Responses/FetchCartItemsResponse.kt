package com.aplication.aquaruim.network.Responses

import com.aplication.aquaruim.models.CartItem
import com.aplication.aquaruim.utils.MResponseStatus

data class FetchCartItemsResponse(val status: MResponseStatus, val cartItems: ArrayList<CartItem>?)
