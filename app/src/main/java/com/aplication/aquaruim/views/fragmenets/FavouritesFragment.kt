package com.aplication.aquaruim.views.fragmenets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.aplication.aquaruim.R
import com.aplication.aquaruim.adapters.FavoritesAdapter
import com.aplication.aquaruim.databinding.FragmentFavouritesBinding
import com.aplication.aquaruim.interfaces.IonAddItemToCart
import com.aplication.aquaruim.models.Food
import com.aplication.aquaruim.viewmodels.FavouritesViewModel

class FavouritesFragment : Fragment() , IonAddItemToCart {

    lateinit var favouritesBinding: FragmentFavouritesBinding;
    private val favouritesViewModel : FavouritesViewModel by activityViewModels();
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


    private fun  watchFavourites( ) {
        this.favouritesBinding.isLoading = true;
        this.favouritesViewModel.getMutableFavourites().observe(viewLifecycleOwner){
            if(it!=null) {
                this.favouritesBinding.favoritesAdapter = FavoritesAdapter(it , this);
                this.favouritesBinding.isEmpty = it.isEmpty()
                this.favouritesBinding.isLoading=false;
            }
        }
    }

    override fun onAddItemCartBtnClicked(food: Food) {
      Toast.makeText(context , "disliking " + food.name , Toast.LENGTH_SHORT).show()
    }

}