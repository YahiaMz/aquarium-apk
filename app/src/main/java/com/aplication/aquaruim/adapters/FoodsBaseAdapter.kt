package com.aplication.aquaruim.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.aplication.aquaruim.R
import com.aplication.aquaruim.databinding.FoodItemContainerBinding
import com.aplication.aquaruim.models.Food

class FoodsBaseAdapter(val foods : ArrayList<Food>) : BaseAdapter() {

    var layoutInflater : LayoutInflater ?= null;
    var foodItemContainerBinding : FoodItemContainerBinding ?=null;

    override fun getCount(): Int {
        return this.foods.size
    }

    override fun getItem(p0: Int): Food {
       return this.foods[p0];
    }

    override fun getItemId(p0: Int): Long {
          return this.foods[p0].id.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        if(layoutInflater == null) {
            layoutInflater = LayoutInflater.from(p2?.context);
        }

        this.foodItemContainerBinding  = DataBindingUtil.inflate(layoutInflater!! , R.layout.food_item_container , null ,false);
        this.foodItemContainerBinding?.cFood = this.foods[p0];
    return foodItemContainerBinding!!.root
    }
}