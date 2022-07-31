package com.aplication.aquaruim.interfaces

import com.aplication.aquaruim.models.CartItem

interface IChangeCartItemQuantity {
    public fun changeCartItemQuantity(item: CartItem , quantity : Int , pos : Int) ;
}