package com.aplication.aquaruim.views.fragmenets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aplication.aquaruim.R
import com.aplication.aquaruim.adapters.FoodsAdapter
import com.aplication.aquaruim.databinding.FragmentSearchBinding
import com.aplication.aquaruim.interfaces.IlikeFood
import com.aplication.aquaruim.interfaces.IonAddItemToCart
import com.aplication.aquaruim.interfaces.IonFoodItemClicked
import com.aplication.aquaruim.models.Food
import com.aplication.aquaruim.viewmodels.CartViewModel
import com.aplication.aquaruim.viewmodels.FavouritesViewModel
import com.aplication.aquaruim.viewmodels.HomeViewModel
import com.aplication.aquaruim.views.activities.FoodDetailsScreen
import com.aplication.aquaruim.views.customViews.SuccessToast
import java.util.*

class SearchFragment : Fragment(), IonAddItemToCart, IonFoodItemClicked, IlikeFood {

    lateinit var searchFragmentBinding: FragmentSearchBinding;
    val homeViewModel: HomeViewModel by activityViewModels()
    val cartViewModel: CartViewModel by activityViewModels();
    val favouritesViewModel: FavouritesViewModel by activityViewModels()

    var allFoodsArrayList: ArrayList<Food> = ArrayList();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        this.searchFragmentBinding = FragmentSearchBinding.inflate(layoutInflater);
        this.onBackPressed();
        if (this.homeViewModel.foodsMutableLiveData.value != null)
            this.allFoodsArrayList = this.homeViewModel.foodsMutableLiveData.value!!.foods!!;
        val initText = this.homeViewModel.textToSearch;
        this.searchFragmentBinding.SearchEditTextSearchFragment.setText(initText);

        val initSearch = allFoodsArrayList.filter { food ->
            food.name.lowercase(
                Locale.getDefault())
                .contains(initText.lowercase(Locale.getDefault()))
        } as ArrayList<Food>

        this.searchFragmentBinding.isThereAnyResult = initSearch.isNotEmpty()

        val searchFoodAdapter = FoodsAdapter(initSearch, this, this, this);
        this.searchFragmentBinding.foodsAdapter = searchFoodAdapter;
        watchSearchEditText();
        return this.searchFragmentBinding.root;

    }

    private fun watchSearchEditText() {
        this.searchFragmentBinding.SearchEditTextSearchFragment.addTextChangedListener {
            val searchTextValue =
                this.searchFragmentBinding.SearchEditTextSearchFragment.text.toString()
                    .lowercase(Locale.getDefault())
            val filteredFoods: ArrayList<Food> = this.allFoodsArrayList.filter { food ->
                food.name.lowercase(
                    Locale.getDefault())
                    .contains(searchTextValue);
            } as ArrayList<Food>

            this.searchFragmentBinding.foodsAdapter?.foods = filteredFoods;
            this.searchFragmentBinding.foodsAdapter?.notifyDataSetChanged()
            this.searchFragmentBinding.isThereAnyResult = filteredFoods.isNotEmpty()

        }
    }


    private fun onBackPressed() {
        this.searchFragmentBinding.backFromSearchFragment.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.mainFrameLayout, HomeFragement()).commit()
        }

    }

    override fun likeFood(food_Id: Int, pos: Int) {
        this.favouritesViewModel.likeFood(food_Id).observe(viewLifecycleOwner) {
            if (it != null) {
                if (it == 1) {
                    this.searchFragmentBinding.foodsAdapter?.foods!![pos].isLiked = true;
                    this.searchFragmentBinding.foodsAdapter?.notifyItemChanged(pos);
                } else if (it == -1) {
                    this.searchFragmentBinding.foodsAdapter?.foods!![pos].isLiked = false;
                    this.searchFragmentBinding.foodsAdapter?.notifyItemChanged(pos)
                }
            }
        }
    }

    override fun onAddItemCartBtnClicked(food: Food, position: Int) {
        this.cartViewModel.addItemToCart(
            food.id,
            if (food.sizes.size != 0) food.sizes[0].id else -1,
            1).observe(viewLifecycleOwner) {
            if (it != null) {
                if (it) {

                    cartViewModel.updateCartItems();
                    cartViewModel.cartCount.value = cartViewModel.cartCount.value!! + 1;
                    SuccessToast.ShowSuccessToast("item added with success ", context!!);
                }
                this.searchFragmentBinding.foodsAdapter?.foods!![position].isAddingItem = false;
                this.searchFragmentBinding.foodsAdapter?.notifyItemChanged(position)
            }
        };

    }


    override fun onFoodItemClicked(food: Food) {
        val toDetailsScreen = Intent(activity, FoodDetailsScreen::class.java)
        toDetailsScreen.putExtra("food", food);
        startActivity(toDetailsScreen)
    }


}