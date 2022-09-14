package com.aplication.aquaruim.views.fragmenets

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aplication.aquaruim.R
import com.aplication.aquaruim.databinding.FragmentCheckoutBinding
import com.aplication.aquaruim.utils.MResponseStatus
import com.aplication.aquaruim.viewmodels.CartViewModel
import com.aplication.aquaruim.viewmodels.OrdersViewModel
import java.util.regex.Pattern

class CheckoutFragment : Fragment() {

    lateinit var checkoutBinding: FragmentCheckoutBinding;
    val ordersViewModel: OrdersViewModel by activityViewModels();
    val cartViewModel: CartViewModel by activityViewModels();
    var zonesAl: ArrayList<String> = ArrayList();
    lateinit var sharedPreferences : SharedPreferences
    var selectedZone = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        this.checkoutBinding = FragmentCheckoutBinding.inflate(layoutInflater);
        sharedPreferences = context!!.getSharedPreferences("USER", Context.MODE_PRIVATE);
        val userPhoneNumber = sharedPreferences.getString("phone_number" , "");

        this.checkoutBinding.phoneNumberEditText.setText(userPhoneNumber)
        onPlaceOrderClicked();
        fillOrderInfo();
        backFromCheckout()
        fillSpinnerAddresses()

        return this.checkoutBinding.root
    }

    private fun isCorrectPhoneNumber(): Boolean {
        val phoneEditText: EditText = this.checkoutBinding.phoneNumberEditText;


        val isMatch = Pattern.compile("0[5,6,7][0-9]{8}")
            .matcher(phoneEditText.text.toString())
            .matches()

        return if (isMatch) {
            true;
        } else {
            phoneEditText.error = "wrong phone number !"
            false; }
    }

    private fun validateZone(): Boolean {
        if (selectedZone == 0) {
            this.checkoutBinding.addressSpinner.setBackgroundResource(R.drawable.spinner_background_error)
            return false;
        }
        return true
    }

    private fun onPlaceOrderClicked() {


        this.checkoutBinding.placeOrder.setOnClickListener {

            if (validateZone() && validateAddress() && isCorrectPhoneNumber()) {
                this.checkoutBinding.isPlacingOrder = true;
                this.ordersViewModel.placeOrder(
                    this.zonesAl[selectedZone],
                    this.checkoutBinding.addressEditText.text.toString().trim(),
                    this.checkoutBinding.phoneNumberEditText.text.toString()).observe(
                    viewLifecycleOwner
                ) {
                    if (it != null && it == true) {
                        this.ordersViewModel.reFetchOrders();
                        prepareCartViewModelAndUpdate();
                    }
                }
            }
        }
    }

    private fun prepareCartViewModelAndUpdate() {
        this.cartViewModel.updateCartItemsForCheckout().observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.status == MResponseStatus.SUCCESS_RESPONSE) {
                    this.cartViewModel.cartCount.value = it.cartItems!!.size;
                    this.cartViewModel.mFragmentIndex.value = 9;
                    this.checkoutBinding.isPlacingOrder = false;
                }
            }
        };
    }

    private fun validateAddress(): Boolean {
        val address = this.checkoutBinding.addressEditText
        if (address.text.trim().length < 5) {
            address.setError("Please enter at least 5 char");
            return false;
        } else return true;
    }

    fun fillOrderInfo() {
        var totalPrice = 0;

        val fCartItems = cartViewModel.mutableCartItems.value!!
        val cartItems =
            if (fCartItems.status == MResponseStatus.SUCCESS_RESPONSE) fCartItems.cartItems!! else ArrayList()
        for (x in 0 until cartItems.size) {
            val cCartItem = cartItems[x]
            totalPrice += cCartItem.quantity * ((if (cCartItem.foodSize == null) cCartItem.food.price else cCartItem.foodSize?.sizePrice)!!)
        }
        this.checkoutBinding.orderTp = totalPrice;

    }

    fun backFromCheckout() {
        this.checkoutBinding.backBtn.setOnClickListener {
            cartViewModel.mFragmentIndex.value = 1
        }
    }

    fun fillSpinnerAddresses() {
        cartViewModel.fetchAreas().observe(viewLifecycleOwner) {
            if (it != null) {
                zonesAl.add("Select a zone");
                zonesAl.addAll(it);
                val spinnerAdapter =
                    ArrayAdapter<String>(requireContext(), R.layout.area_spinner_item, zonesAl)
                spinnerAdapter.setDropDownViewResource(android.R.layout
                    .simple_spinner_dropdown_item)
                this.checkoutBinding.addressSpinner.adapter = spinnerAdapter
                this.checkoutBinding.addressSpinner.setSelection(0)
            }
        }


        this.checkoutBinding.addressSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selectedZone = p2;
                    if (selectedZone != 0) {
                        checkoutBinding.addressSpinner.setBackgroundResource(R.drawable.spinner_background);
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }


    }


}