package com.aplication.aquaruim.views.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aplication.aquaruim.R
import com.aplication.aquaruim.databinding.ActivityMainBinding
import com.aplication.aquaruim.models.CartItem
import com.aplication.aquaruim.utils.API_URLS
import com.aplication.aquaruim.utils.MResponseStatus
import com.aplication.aquaruim.viewmodels.CartViewModel
import com.aplication.aquaruim.viewmodels.FavouritesViewModel
import com.aplication.aquaruim.viewmodels.HomeViewModel
import com.aplication.aquaruim.viewmodels.OrdersViewModel
import com.aplication.aquaruim.views.fragmenets.*
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    lateinit var mainBinding: ActivityMainBinding
    lateinit var homeViewModel: HomeViewModel
    lateinit var cartViewModel: CartViewModel
    lateinit var orderViewModel: OrdersViewModel;
    lateinit var favoritesViewModel: FavouritesViewModel;

    lateinit var sharedPreferences: SharedPreferences;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        this.sharedPreferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
        val userImageProfile = sharedPreferences.getString("imageProfileUrl", "null");

        if (userImageProfile != "null") {
            this.mainBinding.profileItem.also {
                Log.d("PROFILEIMAGE", API_URLS.USER_PROFILE_IMAGE + userImageProfile.toString())
                Picasso.get().load(API_URLS.USER_PROFILE_IMAGE + userImageProfile)
                    .into(it, object : Callback {
                        override fun onSuccess() {
                        }

                        override fun onError(e: Exception?) {
                            mainBinding.profileItem.setBackgroundResource(R.drawable.user_profile);
                        }
                    });
            }
        }

        // preparing view models
        handleCartViewModel()
        handleHomeViewModel();
        prepareOrdersViewModel();
        prepareFavouritesViewModel();
        // end with view models
        mainBinding.index = this.cartViewModel.mFragmentIndex.value;
        replaceFragments();
        loadFragment(HomeFragement());
        watchBottomNavigationIndex();

    }

    override fun onBackPressed() {
        when {
            this.cartViewModel.mFragmentIndex.value!! >= 5 -> {
                this.cartViewModel.mFragmentIndex.value = 0;
            }
            else -> {
                finish();
            }
        }
    }


    private fun prepareFavouritesViewModel() {
        this.favoritesViewModel = ViewModelProvider(this)[FavouritesViewModel::class.java];
        this.favoritesViewModel.fetchFavourites();
    }

    private fun prepareOrdersViewModel() {
        this.orderViewModel = ViewModelProvider(this)[OrdersViewModel::class.java];
        this.orderViewModel.fetchUserOrders();
    }

    private fun handleHomeViewModel() {
        this.homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java];
        this.homeViewModel.fetchFoods();
        this.homeViewModel.fetchCategories();
        this.homeViewModel.fetchSlides();
    }

    private fun handleCartViewModel() {
        this.cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]
        this.cartViewModel.fetchCartItems().observe(
            this
        ) {
            if (it != null) {
                this.cartViewModel.cartCount.value =
                    if (it.status == MResponseStatus.SUCCESS_RESPONSE) calculateInitialCartQuantity(
                        it.cartItems!!) else 0;
            }
        };

        this.cartViewModel.fetchAreas();
        this.cartViewModel.cartCount.observe(this) {
            this.mainBinding.cartItemsCount = it;
        }


    }

    private fun watchBottomNavigationIndex() {
        this.cartViewModel.mFragmentIndex.observe(this) {
            when {
                it == 0 -> {
                    loadFragment(HomeFragement()); this.mainBinding.index = 0
                }
                it == 1 -> {
                    loadFragment(CartFragment()); this.mainBinding.index = 1
                }
                it == 2 -> {
                    loadFragment(FavouritesFragment()); this.mainBinding.index = 2
                }
                it == 3 -> {
                    loadFragment(OrdersFragment()); this.mainBinding.index = 3
                }
                it == 4 -> {
                    loadFragment(UserFragment()); this.mainBinding.index = 4
                }
                it == 6 -> {
                    loadFragment(CheckoutFragment());
                    this.mainBinding.index = 6
                }
                it == 9 -> {
                    loadFragment(OrderPlacedWithSucccessFragment());
                    this.mainBinding.index = 9
                }


            }

        }
    }


    private fun calculateInitialCartQuantity(arrayList: ArrayList<CartItem>): Int {
        var quantity = 0;
        for (x in 0 until arrayList.size) {
            quantity += arrayList[x].quantity;
        }
        return quantity;
    }

    private fun replaceFragments() {

        this.mainBinding.homeItem.setOnClickListener {
            if (this.mainBinding.index != 0) {
                this.cartViewModel.mFragmentIndex.value = 0;
            }

        }
        this.mainBinding.cartItem.setOnClickListener {
            if (this.mainBinding.index != 1) {
                this.cartViewModel.mFragmentIndex.value = 1;
            }
        }
        this.mainBinding.favoritesItem.setOnClickListener {
            if (this.mainBinding.index != 2) {
                this.cartViewModel.mFragmentIndex.value = 2;
            }

        }
        this.mainBinding.ordersItem.setOnClickListener {
            if (this.mainBinding.index != 3) {
                this.cartViewModel.mFragmentIndex.value = 3;
            }
        }

        this.mainBinding.profileItem.setOnClickListener {
            if (this.mainBinding.index != 4) {
                this.cartViewModel.mFragmentIndex.value = 4;
            }
        }

    }


    private fun loadFragment(fragment: Fragment) {
        val translation = supportFragmentManager.beginTransaction()
        translation.replace(R.id.mainFrameLayout, fragment)
        translation.addToBackStack(null)
        translation.commit();
    }


}