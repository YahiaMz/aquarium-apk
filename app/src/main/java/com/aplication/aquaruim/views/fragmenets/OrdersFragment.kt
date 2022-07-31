package com.aplication.aquaruim.views.fragmenets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.aplication.aquaruim.R
import com.aplication.aquaruim.adapters.OrderAdapter
import com.aplication.aquaruim.databinding.FragmentOrdersBinding
import com.aplication.aquaruim.viewmodels.OrdersViewModel

class OrdersFragment : Fragment() {

    private val ordersViewModel : OrdersViewModel by activityViewModels()
    lateinit var ordersBinding: FragmentOrdersBinding;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        this.ordersBinding = DataBindingUtil.inflate(inflater , R.layout.fragment_orders , container ,false);
        this.watchOrders()
        return  this.ordersBinding.root
    }


    public fun watchOrders () {
        this.ordersBinding.isLoading = true;
        this.ordersViewModel.getMutableOrders().observe(viewLifecycleOwner){
            if(it!=null) {
                this.ordersBinding.ordersAdapter = OrderAdapter(it);
                this.ordersBinding.isThereAnyOrder = it.isEmpty()
                this.ordersBinding.isLoading = false;
                this.ordersBinding.ordersRecyclerView.scheduleLayoutAnimation()
            }
        }
    }



}