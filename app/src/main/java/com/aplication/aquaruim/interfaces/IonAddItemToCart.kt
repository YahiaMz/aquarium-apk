package com.aplication.aquaruim.interfaces

import com.aplication.aquaruim.models.Food

interface IonAddItemToCart {
    public fun onAddItemCartBtnClicked(food : Food);
}