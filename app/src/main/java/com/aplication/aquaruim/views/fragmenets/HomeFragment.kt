package com.aplication.aquaruim.views.fragmenets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.aplication.aquaruim.R
import com.aplication.aquaruim.adapters.CategoriesAdapter
import com.aplication.aquaruim.adapters.FoodsAdapter
import com.aplication.aquaruim.databinding.FragmentHomeFragementBinding
import com.aplication.aquaruim.interfaces.IlikeFood
import com.aplication.aquaruim.interfaces.IonAddItemToCart
import com.aplication.aquaruim.interfaces.IonCategorySelected
import com.aplication.aquaruim.interfaces.IonFoodItemClicked
import com.aplication.aquaruim.models.Category
import com.aplication.aquaruim.models.Food
import com.aplication.aquaruim.viewmodels.CartViewModel
import com.aplication.aquaruim.viewmodels.FavouritesViewModel
import com.aplication.aquaruim.viewmodels.HomeViewModel
import com.aplication.aquaruim.views.activities.FoodDetailsScreen
import com.aplication.aquaruim.views.customViews.SuccessToast
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel


class HomeFragement : Fragment(), IonAddItemToCart, IonCategorySelected, IonFoodItemClicked,
    IlikeFood {
    lateinit var homeFragmentBinding: FragmentHomeFragementBinding;
    val homeViewModel: HomeViewModel by activityViewModels()
    val cartViewModel: CartViewModel by activityViewModels();
    val favouritesViewModel: FavouritesViewModel by activityViewModels()
    lateinit var foodAdapter: FoodsAdapter;
    var allFoods = ArrayList<Food>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun fillImagesOfSlider() {
        val slides = ArrayList<SlideModel>();

        slides.add(SlideModel("https://img.freepik.com/photos-premium/pizza-au-pepperoni-isole-blanc_317111-21.jpg?w=996",
            scaleType = ScaleTypes.FIT));
        slides.add(SlideModel("https://img.freepik.com/photos-gratuite/vue-dessus-pizza-au-pepperoni-saucisses-aux-champignons-poivron-olive-mais-bois-noir_141793-2158.jpg?w=1380&t=st=1659305608~exp=1659306208~hmac=b3ed376e75f61df32945fa25c8fcd1e2dc8b8eed5ca9f75b7394951986904bd1",
            scaleType = ScaleTypes.CENTER_CROP));
        slides.add(SlideModel("https://aflaomarket.com/wp-content/uploads/2021/11/burger.jpg?v=d02e7b29555a",
            ScaleTypes.FIT))
        slides.add(SlideModel("https://popmenucloud.com/cdn-cgi/image/width=1920,height=1920,format=auto,fit=scale-down/wmzltkrx/71623eb0-5357-4fc4-8759-167961cf0dbf.jpg",
            ScaleTypes.FIT))

        homeFragmentBinding.mImageSlider.setImageList(slides);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        this.homeFragmentBinding = FragmentHomeFragementBinding.inflate(inflater, container, false);
        fillImagesOfSlider();
        observeFoods()
        this.homeViewModel.showBottomNavigationView.value = true;
        watchCategories();
        onSearchClicked();
        return this.homeFragmentBinding.root
    }

    private fun watchCategories() {
        this.homeFragmentBinding.isLoadingCategories = true;
        this.homeViewModel.categoriesMutableLiveData?.observe(viewLifecycleOwner) {
            if (it != null) {
                val categoriesAl = ArrayList<Category>();
                categoriesAl.add(Category(0, "", "all.png"))
                categoriesAl.addAll(it);
                this.homeFragmentBinding.categoriesAdapter = CategoriesAdapter(this, categoriesAl)
                this.homeFragmentBinding.isLoadingCategories = false
            }
        }
    }


    private fun observeFoods() {
        this.homeFragmentBinding.isLoadingFoods = true;
        this.homeViewModel.foodsMutableLiveData?.observe(viewLifecycleOwner) {

            if (it != null) {
                this.homeFragmentBinding.foodsAdapter = FoodsAdapter(it, this, this, this);
                this.homeFragmentBinding.isLoadingFoods = false;
                this.homeFragmentBinding.foodsItemRecyclerView.scheduleLayoutAnimation();

                allFoods = it;
            }
        };
    }

    override fun onAddItemCartBtnClicked(food: Food) {
        this.cartViewModel.addItemToCart(3,
            food.id,
            if (food.sizes.size != 0) food.sizes[0].id else -1,
            1).observe(viewLifecycleOwner) {
            if (it) {
                cartViewModel.updateCartItems(3);
                cartViewModel.cartCount.value = cartViewModel.cartCount.value!! + 1;
                SuccessToast.ShowSuccessToast("item added with success ", context!!);
            }
        };

    }

    override fun onCategorySelected(category: Category) {


        val filteredFood: ArrayList<Food> = ArrayList();

        if (category.id == 0) {
            filteredFood.addAll(allFoods);
        } else {
            allFoods.forEach { food ->
                run {
                    if (food.categoryId == category.id) filteredFood.add(food);
                }
            }
        }

        this.homeFragmentBinding.foodsAdapter?.foods = filteredFood;
        this.homeFragmentBinding.foodsAdapter?.notifyDataSetChanged();


    }

    override fun onFoodItemClicked(food: Food) {
        val toDetailsScreen = Intent(activity, FoodDetailsScreen::class.java)
        toDetailsScreen.putExtra("food", food);

        startActivity(toDetailsScreen)
    }

    override fun likeFood(food_Id: Int, pos: Int) {
        this.favouritesViewModel.likeFood(food_Id).observe(viewLifecycleOwner) {
            if (it != null) {
                if (it == 1) {
                    this.homeFragmentBinding.foodsAdapter?.foods!![pos].isLiked = true;
                    this.homeFragmentBinding.foodsAdapter?.notifyItemChanged(pos);
                } else if (it == -1) {
                    this.homeFragmentBinding.foodsAdapter?.foods!![pos].isLiked = false;
                    this.homeFragmentBinding.foodsAdapter?.notifyItemChanged(pos)
                }
            }
        }
    }

    private fun onSearchClicked() {
        this.homeFragmentBinding.SearchEditText.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                this.homeViewModel.showBottomNavigationView.value = false;
                this.homeViewModel.textToSearch = this.homeViewModel.textToSearch
                activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.mainFrameLayout, SearchFragment()).commit()
                return@OnEditorActionListener true
            }
            false
        })
    }


}