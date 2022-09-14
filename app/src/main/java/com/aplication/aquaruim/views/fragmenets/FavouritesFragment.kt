package com.aplication.aquaruim.views.fragmenets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aplication.aquaruim.adapters.FavoritesAdapter
import com.aplication.aquaruim.databinding.FragmentFavouritesBinding
import com.aplication.aquaruim.interfaces.IonAddItemToCart
import com.aplication.aquaruim.interfaces.IremoveItemFromFavourites
import com.aplication.aquaruim.models.Food
import com.aplication.aquaruim.viewmodels.CartViewModel
import com.aplication.aquaruim.viewmodels.FavouritesViewModel
import com.aplication.aquaruim.views.customViews.SuccessToast

class FavouritesFragment : Fragment(), IonAddItemToCart, IremoveItemFromFavourites {

    lateinit var favouritesBinding: FragmentFavouritesBinding;
    val cartViewModel: CartViewModel by activityViewModels();

    private val favouritesViewModel: FavouritesViewModel by activityViewModels();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        this.favouritesBinding = FragmentFavouritesBinding.inflate(inflater);
        watchFavourites();
        return this.favouritesBinding.root;
    }


    private fun watchFavourites() {
        this.favouritesBinding.isLoading = true;
        this.favouritesViewModel.getMutableFavourites().observe(viewLifecycleOwner) {
            if (it != null) {
                this.favouritesBinding.favoritesAdapter = FavoritesAdapter(it, this, this);
                this.favouritesBinding.isEmpty = it.isEmpty()
                this.favouritesBinding.isLoading = false;
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
                this.favouritesBinding.favoritesAdapter?.favoriteFoods!![position].isAddingItem  = false
                this.favouritesBinding.favoritesAdapter?.notifyItemChanged(position);
            }
        };

    }

    override fun removeItemFromFavorites(food_Id: Int, pos: Int) {
        this.favouritesViewModel.likeFood(food_Id).observe(viewLifecycleOwner) {
            if (it != null) {
                this.favouritesViewModel.getMutableFavourites().value!!.removeAt(pos);
                this.favouritesBinding.favoritesAdapter?.notifyItemRemoved(pos)
                this.favouritesBinding.favoritesAdapter?.notifyItemRangeChanged(pos,
                    this.favouritesBinding.favoritesAdapter?.favoriteFoods!!.size)
                this.favouritesBinding.isEmpty =
                    this.favouritesBinding.favoritesAdapter?.favoriteFoods!!.isEmpty();
            }
            this.favouritesViewModel.reFetchFavourites()
        }
    }

}