package com.aplication.aquaruim.network.Responses

import com.aplication.aquaruim.models.Food
import com.aplication.aquaruim.utils.MResponseStatus

data class FetchFoodsResponse(val status: MResponseStatus, val foods: ArrayList<Food>?)
