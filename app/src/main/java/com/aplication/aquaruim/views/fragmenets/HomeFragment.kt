package com.aplication.aquaruim.views.fragmenets

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.aplication.aquaruim.utils.API_URLS
import com.aplication.aquaruim.utils.MResponseStatus
import com.aplication.aquaruim.viewmodels.CartViewModel
import com.aplication.aquaruim.viewmodels.FavouritesViewModel
import com.aplication.aquaruim.viewmodels.HomeViewModel
import com.aplication.aquaruim.views.activities.FoodDetailsScreen
import com.aplication.aquaruim.views.customViews.FailToast
import com.aplication.aquaruim.views.customViews.SuccessToast
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel


class HomeFragement : Fragment(), IonAddItemToCart, IonCategorySelected, IonFoodItemClicked,
    IlikeFood {
    lateinit var homeFragmentBinding: FragmentHomeFragementBinding;
    val homeViewModel: HomeViewModel by activityViewModels()
    val cartViewModel: CartViewModel by activityViewModels();
    val favouritesViewModel: FavouritesViewModel by activityViewModels()
    var allFoods = ArrayList<Food>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun fillImagesOfSlider() {
        this.homeViewModel.mutableSlides.observe(viewLifecycleOwner) {
            if (it != null) {
                val slides = ArrayList<SlideModel>();
                for (x in 0 until it.size) {
                    slides.add(SlideModel(API_URLS.SLIDES_IMAGES_URL + it[x], ScaleTypes.FIT));
                }
                this.homeFragmentBinding.isThereAnSlides = slides.isNotEmpty()
                this.homeFragmentBinding.mImageSlider.setImageList(slides);
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        this.homeFragmentBinding = FragmentHomeFragementBinding.inflate(inflater, container, false);
        this.homeFragmentBinding.isThereNetworkProblem = false

        fillImagesOfSlider();
        observeFoods()
        watchCategories();
        onSearchClicked();
        onRefreshClicked()

        return this.homeFragmentBinding.root
    }

    private fun watchCategories() {
        this.homeFragmentBinding.isLoadingCategories = true;
        this.homeFragmentBinding.isThereNetworkProblem= false;
        this.homeViewModel.categoriesMutableLiveData?.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.status == MResponseStatus.SUCCESS_RESPONSE) {
                    val categoriesAl = ArrayList<Category>();
                    categoriesAl.add(Category(0, "", "all.png"))
                    categoriesAl.addAll(it.categories!!);
                    this.homeFragmentBinding.categoriesAdapter =
                        CategoriesAdapter(this, categoriesAl)
                    this.homeFragmentBinding.isThereNetworkProblem = it.categories.isEmpty();
                    this.homeFragmentBinding.isLoadingCategories = false
                    this.homeFragmentBinding.isThereNetworkProblem = false;
                } else if (it.status == MResponseStatus.NO_INTERNET) {
                    this.homeFragmentBinding.isThereNetworkProblem = true;
                }

            }
        }
    }
    private fun observeFoods() {
        this.homeFragmentBinding.isLoadingFoods = true;
        this.homeFragmentBinding.isThereNetworkProblem= false;
        this.homeViewModel.foodsMutableLiveData.observe(viewLifecycleOwner) {

            if (it != null) {
                if (it.status == MResponseStatus.SUCCESS_RESPONSE) {
                    this.homeFragmentBinding.foodsAdapter =
                        FoodsAdapter(it.foods!!, this, this, this);
                    this.homeFragmentBinding.isLoadingFoods = false;
                    this.homeFragmentBinding.foodsItemRecyclerView.scheduleLayoutAnimation();
                    allFoods = it.foods;
                } else if (it.status == MResponseStatus.NO_INTERNET) {
                    this.homeFragmentBinding.isThereNetworkProblem = true
                } else {
                    FailToast.showFailToast("Something wrong", requireContext())
                }
            }
        };
    }

    override fun onAddItemCartBtnClicked(food: Food, position: Int) {

        this.cartViewModel.addItemToCart(
            food.id,
            if (food.sizes.size != 0) food.sizes[0].id else -1,
            1).observe(viewLifecycleOwner) {
            if (it!=null) {
                if(it){
                    cartViewModel.updateCartItems();
                    cartViewModel.cartCount.value = cartViewModel.cartCount.value!! + 1;
                    SuccessToast.ShowSuccessToast("item added with success ", context!!);
                }

                this.homeFragmentBinding.foodsAdapter?.foods!![position].isAddingItem = false
                this.homeFragmentBinding.foodsAdapter?.notifyItemChanged(position);

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
                this.favouritesViewModel.reFetchFavourites()
            }
        }
    }

    private fun onSearchClicked() {
        this.homeFragmentBinding.SearchEditText.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                this.cartViewModel.mFragmentIndex.value = 7;
                this.homeViewModel.textToSearch =
                    this.homeFragmentBinding.SearchEditText.text.toString()
                activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.mainFrameLayout, SearchFragment()).commit()
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun onRefreshClicked() {
        this.homeFragmentBinding.refreshNetwork.setOnClickListener {
            this.homeViewModel.fetchCategories();
            this.homeViewModel.fetchFoods()
            this.homeViewModel.fetchSlides()

            watchCategories();
            observeFoods();
            fillImagesOfSlider();
        }
    }


}