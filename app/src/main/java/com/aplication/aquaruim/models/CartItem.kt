package com.aplication.aquaruim.models

data class CartItem(val id : Int, val food: Food, var foodSize: FoodSize?, var quantity : Int );
