package com.aplication.aquaruim.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aplication.aquaruim.R
import com.aplication.aquaruim.databinding.ActivityMainBinding
import com.aplication.aquaruim.models.CartItem
import com.aplication.aquaruim.viewmodels.CartViewModel
import com.aplication.aquaruim.viewmodels.FavouritesViewModel
import com.aplication.aquaruim.viewmodels.HomeViewModel
import com.aplication.aquaruim.viewmodels.OrdersViewModel
import com.aplication.aquaruim.views.fragmenets.*

class MainActivity : AppCompatActivity() {



    lateinit var mainBinding: ActivityMainBinding
    lateinit var homeViewModel: HomeViewModel
    lateinit var cartViewModel : CartViewModel
    lateinit var orderViewModel: OrdersViewModel;
    lateinit var favoritesViewModel : FavouritesViewModel ;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        this.mainBinding.isBottomNvVisible =true;
        // preparing view models
        handleHomeViewModel();
        handleCartViewModel()
        prepareOrdersViewModel();
        prepareFavouritesViewModel();
       // end with view models
        mainBinding.index = this.homeViewModel.fragmentIndex.value;
        replaceFragments();
        watchVisibilityOfBnV();
        loadFragment(HomeFragement());
        watchBottomNavigationIndex();

    }

    override fun onBackPressed() {
        if(this.homeViewModel.showBottomNavigationView.value == true) {
            finish();
        } else {
            loadFragment(HomeFragement())
        }
         }

    private fun watchVisibilityOfBnV( ){
        this.homeViewModel.showBottomNavigationView.observe(this) {
            this.mainBinding.isBottomNvVisible = it;
        }

    }





    private fun prepareFavouritesViewModel( ) {
        this.favoritesViewModel = ViewModelProvider(this)[FavouritesViewModel::class.java];
        this.favoritesViewModel.fetchFavourites(3);
    }
    private fun prepareOrdersViewModel( ) {
        this.orderViewModel = ViewModelProvider(this)[OrdersViewModel::class.java];
        this.orderViewModel.fetchUserOrders(3);
    }
    private fun handleHomeViewModel( ) {
        this.homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java];
        this.homeViewModel.fetchFoods();
        this.homeViewModel.fetchCategories();
    }
    private fun  handleCartViewModel( ) {
        this.cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]
        this.cartViewModel.fetchCartItems(3).observe(
            this
        ){
            if(it!=null) {
                this.cartViewModel.cartCount.value = calculateInitialCartQuantity(it);
            }
        };

        this.cartViewModel.cartCount.observe(this) {
            this.mainBinding.cartItemsCount = it;
        }



    }

    private fun watchBottomNavigationIndex() {
        this.homeViewModel.fragmentIndex.observe(this) {
          when {
              it == 0 -> loadFragment(HomeFragement())
              it == 1 -> loadFragment(CartFragment())
              it == 2 -> loadFragment(FavouritesFragment())
              it == 3 -> loadFragment(OrdersFragment())
          }

        }
    }
    private fun calculateInitialCartQuantity ( arrayList: ArrayList<CartItem>) : Int {
        var quantity = 0;
        for (x in 0 until arrayList.size) {
            quantity+=arrayList[x].quantity;
        }
        return quantity;
    }

    private fun replaceFragments() {

        this.mainBinding.homeItem.setOnClickListener {
            if (this.mainBinding.index != 0) {
                this.mainBinding.index = 0;
                this.homeViewModel.fragmentIndex.value = 0;
//                loadFragment(HomeFragement())
            }

        }
        this.mainBinding.cartItem.setOnClickListener {
            if (this.mainBinding.index != 1) {
                this.mainBinding.index = 1;
                this.homeViewModel.fragmentIndex.value = 1;
            }
        }
        this.mainBinding.favoritesItem.setOnClickListener {
            if(this.mainBinding.index != 2) {
                this.mainBinding.index = 2;
                loadFragment(FavouritesFragment())
            }

        }
        this.mainBinding.ordersItem.setOnClickListener {
            if(this.mainBinding.index != 3) {
                this.mainBinding.index = 3;
                this.homeViewModel.fragmentIndex.value = 3;
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