package com.aplication.aquaruim.interfaces

import com.aplication.aquaruim.models.CartItem

interface IChangeCartFoodSize {
    public fun changeFoodInCartSize( cartItemId : Int ,  newSizeId : Int , position: Int)
}