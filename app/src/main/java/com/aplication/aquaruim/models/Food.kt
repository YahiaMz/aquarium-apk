package com.aplication.aquaruim.models

import java.io.Serializable

data class Food(val id : Int, val categoryId : Int ?, val  name : String, val price : Int?, val description : String?, val imageUrl : String, val sizes :ArrayList<FoodSize>, var isLiked : Boolean?) : Serializable;
