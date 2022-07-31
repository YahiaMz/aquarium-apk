package com.aplication.aquaruim.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.aplication.aquaruim.models.Order
import com.aplication.aquaruim.repositories.OrdersRepository

class OrdersViewModel public constructor( application: Application): AndroidViewModel(application){
    @SuppressLint("StaticFieldLeak")
    val mContext : Context = application.applicationContext;
    val mOrdersRepository = OrdersRepository.getInstance(mContext);


    private var mMutableOrders : MutableLiveData<ArrayList<Order>> ?=null;


    public fun placeOrder(userId : Int , address: String , orderPhoneNumber : String) : MutableLiveData<Boolean> {
       return this.mOrdersRepository.placeOrder(userId , address , orderPhoneNumber);
    }

    public fun fetchUserOrders( user_Id : Int ) : MutableLiveData<ArrayList<Order>> {
        if(this.mMutableOrders == null) {
            this.mOrdersRepository.fetchOrdersOfUser(user_Id);
            this.mMutableOrders = this.mOrdersRepository.mutableOrders;
        }
        return this.mMutableOrders!!;
    }

    public fun  updateOrders(user_Id : Int) {
        this.mOrdersRepository.fetchOrdersOfUser(user_Id)
    }

    public fun getMutableOrders()  : MutableLiveData<ArrayList<Order>> {
        return this.mMutableOrders!!;
    }






}