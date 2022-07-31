package com.aplication.aquaruim.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.aplication.aquaruim.models.Food
import com.aplication.aquaruim.models.FoodSize
import com.aplication.aquaruim.repositories.FavoritesRepository
import com.aplication.aquaruim.utils.API_URLS
import org.json.JSONArray
import org.json.JSONObject

class FavouritesViewModel : AndroidViewModel   {

    private var mutableFavourites : MutableLiveData<ArrayList<Food>> ?= null;

    @SuppressLint("StaticFieldLeak")
    val fContext : Context ;
    val favoritesRepository : FavoritesRepository;

     constructor(application: Application) : super(application) {
        this.fContext = application.applicationContext;
        this.favoritesRepository = FavoritesRepository.getInstance(fContext);
    }

     fun fetchFavourites ( user_Id : Int) {
        if(mutableFavourites == null) {
            this.mutableFavourites = this.favoritesRepository.fetchFavouritesFoods(user_Id);
        }
    }


     fun getMutableFavourites( ) : MutableLiveData<ArrayList<Food>> {
        return  this.mutableFavourites!!;
    }

     fun fetchAgainToUpdate( user_Id: Int ){
        this.mutableFavourites = this.favoritesRepository.fetchFavouritesFoods(user_Id);
    }

    fun likeFood(  food_Id : Int ) : MutableLiveData<Int> {
        return this.favoritesRepository.likeFood(3 , food_Id);
    }




}