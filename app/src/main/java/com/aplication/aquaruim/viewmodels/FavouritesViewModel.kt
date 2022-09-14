package com.aplication.aquaruim.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
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

class FavouritesViewModel(application: Application) : AndroidViewModel(application) {

    private var mutableFavourites : MutableLiveData<ArrayList<Food>> ?= null;

    @SuppressLint("StaticFieldLeak")
    val fContext : Context = application.applicationContext
    val favoritesRepository : FavoritesRepository = FavoritesRepository.getInstance(fContext);


    val sharedPreferences: SharedPreferences =
        fContext.getSharedPreferences("USER", Context.MODE_PRIVATE);
    val user_Id = sharedPreferences.getInt("user_id" , -1)
     fun fetchFavourites (  ) {
        if(mutableFavourites == null) {
            this.mutableFavourites = this.favoritesRepository.fetchFavouritesFoods(user_Id);
        }
    }

    fun reFetchFavourites (  ) {
            this.mutableFavourites = this.favoritesRepository.fetchFavouritesFoods(user_Id);
    }




    fun getMutableFavourites( ) : MutableLiveData<ArrayList<Food>> {
        return  this.mutableFavourites!!;
    }

     fun fetchAgainToUpdate(  ){
        this.mutableFavourites = this.favoritesRepository.fetchFavouritesFoods(this.user_Id);
    }

    fun likeFood(  food_Id : Int ) : MutableLiveData<Int> {
        return this.favoritesRepository.likeFood(this.user_Id , food_Id);
    }




}