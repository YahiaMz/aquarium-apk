package com.aplication.aquaruim.network.Responses

import com.aplication.aquaruim.models.Category
import com.aplication.aquaruim.utils.MResponseStatus

data class FetchCategoriesResponse (val status: MResponseStatus, val categories: ArrayList<Category>?)
