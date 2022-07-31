package com.aplication.aquaruim.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.aplication.aquaruim.R
import com.aplication.aquaruim.databinding.ActivityCheckoutBinding
import com.aplication.aquaruim.viewmodels.CartViewModel
import com.aplication.aquaruim.viewmodels.HomeViewModel
import com.aplication.aquaruim.viewmodels.OrdersViewModel
import java.util.regex.Pattern

class CheckoutActivity : AppCompatActivity() {
    lateinit var activityCheckoutBinding : ActivityCheckoutBinding;
    lateinit var ordersViewModel: OrdersViewModel ;
    private lateinit var cartViewModel : CartViewModel ;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.activityCheckoutBinding = DataBindingUtil.setContentView(this , R.layout.activity_checkout );
        this.ordersViewModel = ViewModelProvider(this)[OrdersViewModel::class.java];

        this.onMyBackBtnPressed();
        this.onPlaceOrderClicked();


    }

    private fun  onMyBackBtnPressed() {
        this.activityCheckoutBinding.backBtn.setOnClickListener {
            this.onBackPressed();
        }
    }
    private fun watchAddressEditTexInputs( ) =
        this.activityCheckoutBinding.phoneNumberEditText.addTextChangedListener{ _ ->
            run {
                if (isCorrectPhoneNumber() ) {
                    this.activityCheckoutBinding.phoneNumberEditText.setBackgroundResource(R.drawable.correct_phone_number);
                } else {

                    this.activityCheckoutBinding.phoneNumberEditText.setBackgroundResource(R.drawable.edit_text_address_bg);
                }
            }
        }


    private fun  isCorrectPhoneNumber (   ) : Boolean {
        val phoneEditText : EditText = this.activityCheckoutBinding.phoneNumberEditText;


        val isMatch =  Pattern.compile("0[5,6,7][0-9]{8}")
            .matcher(phoneEditText.text.toString())
            .matches()

        return if(isMatch) {
           true;
        } else {
            phoneEditText.error = "wrong phone number !"
            false;        }
    }

    private fun onPlaceOrderClicked() {
        this.activityCheckoutBinding.placeOrder.setOnClickListener {
         if( validateAddress() && isCorrectPhoneNumber()) {
             this.ordersViewModel.placeOrder(3 , activityCheckoutBinding.addressEditText.text.toString() , activityCheckoutBinding.phoneNumberEditText.text.toString()).observe(
                 this
             ) {
                 if(it != null && it == true) {
                     val toSuccessActivity: Intent = Intent(this, CongratulationOrderPlacedWithSuccessActivity::class.java);
                     this.ordersViewModel.updateOrders(3);
                     prepareCartViewModelAndUpdate();
                     finish();
                     startActivity(toSuccessActivity);
                 }
                 }
         }
        }
    }

    private fun prepareCartViewModelAndUpdate() {
        this.cartViewModel = ViewModelProvider(this)[CartViewModel::class.java];
        this.cartViewModel.updateCartItemsForCheckout().observe(this) {
            if(it!=null) {
                this.cartViewModel.cartCount.value = 0;
            }
        };
    }

    private  fun  validateAddress( ) : Boolean {
        val address = this.activityCheckoutBinding.addressEditText
        if(address.text.length < 10) {
            address.setError("address must be at least 10 char");
            return false;
        }else return  true;
    }

}