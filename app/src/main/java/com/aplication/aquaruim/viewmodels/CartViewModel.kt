package com.aplication.aquaruim.viewmodels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.aplication.aquaruim.models.FoodSize
import com.aplication.aquaruim.network.Responses.FetchCartItemsResponse
import com.aplication.aquaruim.repositories.CartItemRepository
import com.aplication.aquaruim.utils.MResponseStatus

class CartViewModel : AndroidViewModel {

    var cartItemRepository: CartItemRepository;
    val mFragmentIndex: MutableLiveData<Int> = MutableLiveData(0);
    val mContext: Context = getApplication<Application>().applicationContext;
    val sharedPreferences: SharedPreferences =
        mContext.getSharedPreferences("USER", Context.MODE_PRIVATE);
    val user_Id = sharedPreferences.getInt("user_id", -1);
    val token = sharedPreferences.getString("token", "no token");


    constructor(application: Application) : super(application) {
        this.cartItemRepository = CartItemRepository.getInstance(mContext);
    }

    var cartCount: MutableLiveData<Int> = MutableLiveData<Int>(0);
    var mutableCartItems: MutableLiveData<FetchCartItemsResponse> = MutableLiveData(null);


    public fun fetchCartItems(): MutableLiveData<FetchCartItemsResponse> {
        if (mutableCartItems.value == null || mutableCartItems.value!!.status != MResponseStatus.SUCCESS_RESPONSE) {

            this.cartItemRepository.fetchCartItemsOfUser(this.user_Id, token.toString());
            this.mutableCartItems = this.cartItemRepository.mutableCartItems;

        }
        return mutableCartItems;
    }


    public fun updateCartItems(): MutableLiveData<FetchCartItemsResponse> {
        this.cartItemRepository.fetchCartItemsOfUser(this.user_Id, token.toString());
        var nMutableCartItems: MutableLiveData<FetchCartItemsResponse> = MutableLiveData(null)
        nMutableCartItems = this.cartItemRepository.mutableCartItems;

        this.mutableCartItems = nMutableCartItems;
        return nMutableCartItems;
    }

    public fun updateCartItemsForCheckout(): MutableLiveData<FetchCartItemsResponse> {
        return this.cartItemRepository.fetchCartItemsOfUser(this.user_Id, token.toString());
    }


    public fun addItemToCart(
        food_Id: Int,
        food_Size_Id: Int,
        quantity: Int,
    ): MutableLiveData<Boolean> {

        Log.d("ADD_ITEM" , "CartViewModel")
        return this.cartItemRepository.addToCart(user_Id = this.user_Id,
            food_Id = food_Id,
            quantity = quantity,
            foodSize_Id = food_Size_Id,
            token = token.toString()
        );
    }

    public fun removeFoodFromUserCart(cart_Item: Int): MutableLiveData<Boolean> {
        return this.cartItemRepository.removeFromCart(cart_Item, token.toString());
    }


    public fun changeCartItemQuantity(cart_ItemId: Int, quantity: Int): MutableLiveData<Int> {
        return this.cartItemRepository.changeItemQuantity(cart_ItemId, quantity, token.toString());
    }

    public fun changeFoodSize(cart_ItemId: Int, food_Size_Id: Int): MutableLiveData<FoodSize> {
        return this.cartItemRepository.changeFoodCartItemSize(this.user_Id,
            cart_ItemId,
            food_Size_Id,
            token.toString());
    }


    var mutableAreas: MutableLiveData<ArrayList<String>> = MutableLiveData(ArrayList())
    public fun fetchAreas(): MutableLiveData<ArrayList<String>> {
        if (mutableAreas.value!!.isEmpty()) {
            this.mutableAreas = this.cartItemRepository.fetchAreas();
        }
        return this.mutableAreas;
    }
}