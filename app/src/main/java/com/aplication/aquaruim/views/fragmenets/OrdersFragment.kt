package com.aplication.aquaruim.views.fragmenets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import com.aplication.aquaruim.R
import com.aplication.aquaruim.adapters.OrderAdapter
import com.aplication.aquaruim.databinding.FragmentOrdersBinding
import com.aplication.aquaruim.interfaces.IReceiveOrder
import com.aplication.aquaruim.utils.MResponseStatus
import com.aplication.aquaruim.viewmodels.OrdersViewModel

class OrdersFragment : Fragment(), IReceiveOrder {

    private val ordersViewModel: OrdersViewModel by activityViewModels()
    lateinit var ordersBinding: FragmentOrdersBinding;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        this.ordersBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_orders, container, false);
        ordersBinding.isThereNetworkProblem = false
        this.watchOrders()
        onRefresh();
        refreshNetwork();
        setFragmentResultListener("YES_RECEIVED") { requestKey, bundle ->

            val orderId = bundle.getInt("order_Id", -1);
            val pos = bundle.getInt("pos", -1)
            if (orderId != -1 && pos != -1)
                this.ordersViewModel.orderReceived(orderId).observe(viewLifecycleOwner) {
                    if (it != null) {
                        if (it) {
                            this.ordersViewModel.getMutableOrders().value?.orders!![pos].status = 5;
                            this.ordersViewModel.getMutableOrders().value?.orders!![pos].isReceived = true;
                            this.ordersBinding.ordersAdapter?.notifyItemChanged(pos);
                            this.ordersBinding.isThereAnyOrder = true
                        }
                    }
                }
        }
        return this.ordersBinding.root
    }

    private fun onRefresh() {
        this.ordersBinding.refreshOrders.setOnRefreshListener {
            refreshOrders();
        }
    }

    fun refreshOrders() {
        this.ordersViewModel.reFetchOrders().observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.status == MResponseStatus.SUCCESS_RESPONSE) {
                    this.ordersBinding.ordersAdapter?.orders!!.clear()
                    this.ordersBinding.ordersAdapter?.orders!!.addAll(it.orders!!);
                    this.ordersBinding.ordersAdapter?.notifyDataSetChanged();
                    this.ordersBinding.ordersRecyclerView.scheduleLayoutAnimation()
                } else if (it.status == MResponseStatus.NO_INTERNET) {
                    ordersBinding.isThereNetworkProblem = true
                }

                this.ordersBinding.refreshOrders.isRefreshing = false;
                this.ordersBinding.isLoading = false;
            }
        }
    }

    fun watchOrders() {
        this.ordersBinding.isLoading = true;
        this.ordersViewModel.getMutableOrders().observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.status == MResponseStatus.SUCCESS_RESPONSE) {
                    this.ordersBinding.ordersAdapter = OrderAdapter(it.orders!!, this);
                    this.ordersBinding.isThereAnyOrder = it.orders.isNotEmpty()
                    this.ordersBinding.ordersRecyclerView.scheduleLayoutAnimation()
                } else if (it.status == MResponseStatus.NO_INTERNET) {
                    Toast.makeText(context , " NO network " , Toast.LENGTH_SHORT).show()
                    ordersBinding.isThereNetworkProblem = true
                }

                this.ordersBinding.isLoading = false;
            }
        }
    }

    fun refreshNetwork() {
        this.ordersBinding.refreshNetworkOrders.setOnClickListener {
            refreshOrders()
        }
    }

    override fun onReceiveOrder(orderId: Int, pos: Int) {
        val yesNoDialog = YesNoDialog(orderId, pos)
        yesNoDialog.show(parentFragmentManager, null);

    }


}