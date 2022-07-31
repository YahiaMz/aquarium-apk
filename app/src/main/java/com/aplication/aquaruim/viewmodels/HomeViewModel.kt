package com.aplication.aquaruim.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.aplication.aquaruim.models.Category
import com.aplication.aquaruim.models.Food
import com.aplication.aquaruim.repositories.HomeRepository

class HomeViewModel public constructor( application: Application) : AndroidViewModel(application)  {



    var fragmentIndex : MutableLiveData<Int>  = MutableLiveData(0);
    var textToSearch  = "";

    var showBottomNavigationView : MutableLiveData<Boolean> = MutableLiveData(true)
    var foodsMutableLiveData: MutableLiveData<ArrayList<Food>> ? = null;
    var categoriesMutableLiveData : MutableLiveData<ArrayList<Category>>?= null;

    @SuppressLint("StaticFieldLeak")
      final val  mContext : Context = application.applicationContext;
      private  val foodsRepository : HomeRepository = HomeRepository.getInstance(mContext)


    public fun fetchFoods() : MutableLiveData<ArrayList<Food>> {
        if(this.foodsMutableLiveData == null) {
            this.foodsMutableLiveData = this.foodsRepository.fetchFoodsToUser(3);
        }
   return  foodsMutableLiveData!!;
    }

    public  fun fetchCategories() : MutableLiveData<ArrayList<Category>> {
        if(this.categoriesMutableLiveData == null) {
            this.categoriesMutableLiveData = this.foodsRepository.fetchCategories()
        }
        return this.categoriesMutableLiveData!!;
    }






}