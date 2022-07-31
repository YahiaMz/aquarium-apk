package com.aplication.aquaruim.interfaces

import com.aplication.aquaruim.models.CartItem
import com.aplication.aquaruim.models.Food

interface IonRemoveItemFromCart {
    public fun onRemoveItemCartBtnClicked(cartItem_Id : Int , quantity : Int, pos : Int);

}