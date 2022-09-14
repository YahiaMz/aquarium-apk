package com.aplication.aquaruim.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.aplication.aquaruim.network.Responses.FetchOrdersResponse
import com.aplication.aquaruim.repositories.OrdersRepository

class OrdersViewModel constructor(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    val mContext: Context = application.applicationContext;
    val mOrdersRepository = OrdersRepository.getInstance(mContext);

    val sharedPreferences: SharedPreferences =
        mContext.getSharedPreferences("USER", Context.MODE_PRIVATE);
    val user_Id = sharedPreferences.getInt("user_id", -1);
    val token = sharedPreferences.getString("token", "no Token");


    private var mMutableOrders: MutableLiveData<FetchOrdersResponse> = MutableLiveData(null);


    public fun placeOrder(
        zone: String,
        address: String,
        orderPhoneNumber: String,
    ): MutableLiveData<Boolean> {
        return this.mOrdersRepository.placeOrder(this.user_Id,
            zone,
            address,
            orderPhoneNumber,
            token = token.toString());
    }

    fun fetchUserOrders(): MutableLiveData<FetchOrdersResponse> {
        if (this.mMutableOrders.value == null) {
            this.mMutableOrders = this.mOrdersRepository.fetchOrdersOfUser(this.user_Id, token);
        }
        return this.mMutableOrders!!;
    }


    fun orderReceived(orderId: Int): MutableLiveData<Boolean> {
        return this.mOrdersRepository.ReceiveOrder(orderId , token.toString())
    }


    fun getMutableOrders(): MutableLiveData<FetchOrdersResponse> {
        return this.mMutableOrders;
    }

    fun reFetchOrders(): MutableLiveData<FetchOrdersResponse> {
        var rMutableOrders = this.mOrdersRepository.fetchOrdersOfUser(user_Id, token)
        this.mMutableOrders = rMutableOrders;
        return rMutableOrders;
    }


}