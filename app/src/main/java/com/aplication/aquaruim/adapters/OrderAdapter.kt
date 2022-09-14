package com.aplication.aquaruim.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.aplication.aquaruim.databinding.OrderContainerBinding
import com.aplication.aquaruim.interfaces.IReceiveOrder
import com.aplication.aquaruim.models.Order

class OrderAdapter public constructor(
    val orders: ArrayList<Order>,
    val iReceivedOrder: IReceiveOrder,
) :
    RecyclerView.Adapter<OrderAdapter.OrderHolder>() {


    var layoutInflater: LayoutInflater? = null;
    lateinit var orderContainerBinding: OrderContainerBinding;

    public class OrderHolder public constructor(var itemBinding: OrderContainerBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        public fun bind(order: Order) {


            val layoutParams: ConstraintLayout.LayoutParams =
                ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 16, 0, 16);
            itemBinding.root.layoutParams = layoutParams;


            itemBinding.order = order;

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
        if (layoutInflater == null) {
            this.layoutInflater = LayoutInflater.from(parent.context);
        }

        this.orderContainerBinding = OrderContainerBinding.inflate(layoutInflater!!);
        return OrderHolder(this.orderContainerBinding);


    }

    override fun onBindViewHolder(holder: OrderHolder, position: Int) {
        val order = orders[position];
        holder.bind(order)
        this.orderContainerBinding.ReceiveOrder.setOnClickListener {
            if (!order.isReceived) {
                this.iReceivedOrder.onReceiveOrder(
                    order.id,
                    position
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return this.orders.size
    }


}


