package com.aplication.aquaruim.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.aplication.aquaruim.R
import com.aplication.aquaruim.databinding.ActivityFoodDetailsScreenBinding
import com.aplication.aquaruim.models.Food
import com.aplication.aquaruim.utils.API_URLS
import com.aplication.aquaruim.viewmodels.CartViewModel
import com.aplication.aquaruim.views.customViews.SuccessToast
import com.squareup.picasso.Picasso

class FoodDetailsScreen : AppCompatActivity() {

    lateinit var comingData: Bundle;
    lateinit var screenDetailsBinding: ActivityFoodDetailsScreenBinding;
    lateinit var cartViewModel: CartViewModel;
    lateinit var foodObj: Food;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.screenDetailsBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_food_details_screen);
        this.screenDetailsBinding.quantity = 1;
        this.cartViewModel = ViewModelProvider(this)[CartViewModel::class.java];

        this.screenDetailsBinding.foodSizeIndex = 0;
        this.comingData = intent.extras!!
        setFoodInfo();
        oNmBackPressed();
        changeQuantity();
        changeSizeTo()
        addToCart()

    }

    fun oNmBackPressed() {
        this.screenDetailsBinding.backFromDetails.setOnClickListener {
            finish();
        }
    }

    private fun setFoodInfo() {
        foodObj = comingData.getSerializable("food") as Food;
        this.screenDetailsBinding.food = foodObj;
        Picasso.get().load(API_URLS.FOOD_IMAGES_URL + foodObj.imageUrl)
            .into(this.screenDetailsBinding.imageOfPizzaDetails);

    }

    private fun changeSizeTo() {

        this.screenDetailsBinding.mediumTvButton.setOnClickListener {
            this.screenDetailsBinding.foodSizeIndex = 1;
        }
        this.screenDetailsBinding.smallTvButton.setOnClickListener {
            this.screenDetailsBinding.foodSizeIndex = 0;
        }
        this.screenDetailsBinding.largeTvButton.setOnClickListener {
            this.screenDetailsBinding.foodSizeIndex = 2;
        }

    }

    private fun changeQuantity() {
        this.screenDetailsBinding.decreaseQuantityButton.setOnClickListener {
            if (this.screenDetailsBinding.quantity!! > 1) {
                this.screenDetailsBinding.quantity = this.screenDetailsBinding.quantity!! - 1;
            }
        }
        this.screenDetailsBinding.increaseQuantityButton.setOnClickListener {
            this.screenDetailsBinding.quantity = this.screenDetailsBinding.quantity!! + 1;
        }

    }

    private fun addToCart() {


        this.screenDetailsBinding.addToCartFromDetails.setOnClickListener {
            this.screenDetailsBinding.isAddingToCart = true
            var foodSize_Id = -1;
            if (foodObj.sizes.size != 0) foodSize_Id =
                foodObj.sizes[this.screenDetailsBinding.foodSizeIndex!!].id
            this.cartViewModel.addItemToCart(
                foodObj.id,
                foodSize_Id,
                this.screenDetailsBinding.quantity!!).observe(this) {
                if (it != null) {
                    if (it) {
                        val message =
                            if (this.screenDetailsBinding.quantity!! == 1) "added ${this.screenDetailsBinding.quantity!!} item to cart" else "added ${this.screenDetailsBinding.quantity!!} items to cart";
                        SuccessToast.ShowSuccessToast(message,
                            this);
                        this.cartViewModel.updateCartItems().observe(this) {

                        }
                    }

                    screenDetailsBinding.isAddingToCart =false

                }
            };

        }
    }
}