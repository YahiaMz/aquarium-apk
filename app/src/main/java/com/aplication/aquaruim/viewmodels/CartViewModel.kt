package com.aplication.aquaruim.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.aplication.aquaruim.models.CartItem
import com.aplication.aquaruim.models.FoodSize
import com.aplication.aquaruim.repositories.CartItemRepository

class CartViewModel  : AndroidViewModel {

    var cartItemRepository : CartItemRepository;

    val mContext : Context = getApplication<Application>().applicationContext;


    constructor(application: Application) : super(application) {
        this.cartItemRepository  = CartItemRepository.getInstance(mContext);
    }

    var cartCount : MutableLiveData<Int> = MutableLiveData<Int> (0);
    var mutableCartItems : MutableLiveData<ArrayList<CartItem>>? =null;


    public fun fetchCartItems (  user_Id : Int ) : MutableLiveData<ArrayList<CartItem>>  {
        if(mutableCartItems == null) {
             this.cartItemRepository.fetchCartItemsOfUser(user_Id);
             this.mutableCartItems = this.cartItemRepository.mutableCartItems;
        }
     return mutableCartItems!!;
    }


    public fun updateCartItems (  user_Id : Int ) : MutableLiveData<ArrayList<CartItem>> {
        this.cartItemRepository.fetchCartItemsOfUser(user_Id);
        this.mutableCartItems = this.cartItemRepository.mutableCartItems;
        return this.mutableCartItems!!;
    }

    public fun updateCartItemsForCheckout (    ) : MutableLiveData<ArrayList<CartItem>> {
        return  this.cartItemRepository.fetchCartItemsOfUser(3);

    }


    public fun addItemToCart (user_Id: Int , food_Id : Int , food_Size_Id : Int , quantity:Int  ) : MutableLiveData<Boolean> {
        return this.cartItemRepository.addToCart(user_Id = user_Id , food_Id = food_Id , quantity = quantity , foodSize_Id = food_Size_Id);
    }

    public fun removeFoodFromUserCart( cart_Item : Int) : MutableLiveData<Boolean> {
        return this.cartItemRepository.removeFromCart(cart_Item);
    }


    public fun  changeCartItemQuantity(cart_ItemId : Int , quantity: Int)  : MutableLiveData<Int> {
        return this.cartItemRepository.changeItemQuantity(cart_ItemId , quantity);
    }

    public fun changeFoodSize( cart_ItemId: Int , food_Size_Id: Int) : MutableLiveData<FoodSize> {
        return this.cartItemRepository.changeFoodCartItemSize(3 , cart_ItemId , food_Size_Id);
    }


}