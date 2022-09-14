package com.aplication.aquaruim.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.aplication.aquaruim.models.Category
import com.aplication.aquaruim.models.Food
import com.aplication.aquaruim.network.Responses.FetchCategoriesResponse
import com.aplication.aquaruim.network.Responses.FetchFoodsResponse
import com.aplication.aquaruim.repositories.HomeRepository
import com.aplication.aquaruim.utils.MResponseStatus

class HomeViewModel public constructor(application: Application) : AndroidViewModel(application) {


    var textToSearch = "";

    var foodsMutableLiveData: MutableLiveData<FetchFoodsResponse> = MutableLiveData(null);
    var categoriesMutableLiveData: MutableLiveData<FetchCategoriesResponse> = MutableLiveData(null);
    var mutableSlides: MutableLiveData<ArrayList<String>> = MutableLiveData(ArrayList());

    @SuppressLint("StaticFieldLeak")
    final val mContext: Context = application.applicationContext;
    private val foodsRepository: HomeRepository = HomeRepository.getInstance(mContext)


    val sharedPreferences: SharedPreferences =
        mContext.getSharedPreferences("USER", Context.MODE_PRIVATE);
    val user_Id = sharedPreferences.getInt("user_id", -1);


    public fun fetchFoods(): MutableLiveData<FetchFoodsResponse> {
        if (this.foodsMutableLiveData.value == null || this.foodsMutableLiveData.value!!.status != MResponseStatus.SUCCESS_RESPONSE  ) {
            this.foodsMutableLiveData = this.foodsRepository.fetchFoodsToUser(this.user_Id);
        }
        return foodsMutableLiveData;
    }

    public fun fetchCategories(): MutableLiveData<FetchCategoriesResponse> {
        if (this.categoriesMutableLiveData.value == null || this.categoriesMutableLiveData.value!!.status != MResponseStatus.SUCCESS_RESPONSE) {
            this.categoriesMutableLiveData = this.foodsRepository.fetchCategories()
        }
        return this.categoriesMutableLiveData;
    }

    public fun fetchSlides(): MutableLiveData<ArrayList<String>> {
        if (mutableSlides.value == null || mutableSlides.value!!.isEmpty()) {
            this.mutableSlides = foodsRepository.fetchSlides();
        }
        return this.mutableSlides
    }


}