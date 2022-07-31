package com.aplication.aquaruim.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aplication.aquaruim.databinding.OrderItemContainerBinding
import com.aplication.aquaruim.models.OrderItem
import com.aplication.aquaruim.utils.API_URLS
import com.squareup.picasso.Picasso

class OrderItemAdapter public constructor(val orderItems : ArrayList<OrderItem> ) :
    RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {

    var layoutInflater :LayoutInflater ?=null;
    lateinit var orderItemContainerBinding: OrderItemContainerBinding;

    public class OrderItemViewHolder (private val orderItemContainerBinding: OrderItemContainerBinding) : ViewHolder(orderItemContainerBinding.root) {
        public  fun bindOrderItem ( orderItem: OrderItem ) {
            this.orderItemContainerBinding.orderItem = orderItem;

            val layoutParams : ConstraintLayout.LayoutParams =
                ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(16, 0 , 16 , 0);
            orderItemContainerBinding.root.layoutParams =layoutParams;


            Picasso.get().load(API_URLS.FOOD_IMAGES_URL+orderItem.foodImage).into(this.orderItemContainerBinding.orderItemImage);
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        if(this.layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context);
        }
        this.orderItemContainerBinding = OrderItemContainerBinding.inflate(layoutInflater!!);
        return OrderItemViewHolder(orderItemContainerBinding);

    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val orderItem = this.orderItems[position]

        holder.bindOrderItem(orderItem)
    }

    override fun getItemCount(): Int {
     return this.orderItems.size
    }


}