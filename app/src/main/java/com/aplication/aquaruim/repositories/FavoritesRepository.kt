package com.aplication.aquaruim.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.aplication.aquaruim.models.Food
import com.aplication.aquaruim.models.FoodSize
import com.aplication.aquaruim.utils.API_URLS
import com.aplication.aquaruim.views.customViews.FailToast
import org.json.JSONArray
import org.json.JSONObject

class FavoritesRepository private constructor(val fContext: Context) {


    companion object {

        private var instance: FavoritesRepository? = null;
        public fun getInstance(context: Context): FavoritesRepository {
            if (instance == null) {
                this.instance = FavoritesRepository(context);
            }
            return instance!!;
        }

    }


    public fun fetchFavouritesFoods(user_Id: Int): MutableLiveData<ArrayList<Food>> {
        //Toast.makeText(context , "fetching data ", Toast.LENGTH_SHORT).show()
        val mutableLiveDataFavourites: MutableLiveData<ArrayList<Food>> = MutableLiveData(null);
        val fetchFavoritesFoodsRequest: StringRequest =
            StringRequest(Request.Method.GET, API_URLS.FAVORITES_URL + user_Id.toString(),
                {
                    val jsonResponse: JSONObject = JSONObject(it);
                    if (jsonResponse.getBoolean("success")) {
                        val favoritesFoodsArrayList: ArrayList<Food> = ArrayList<Food>();
                        val favoriteJSONFoods: JSONArray = jsonResponse.getJSONArray("message");


                        for (x in 0 until favoriteJSONFoods.length()) {
                            val cFavorite: JSONObject = favoriteJSONFoods.getJSONObject(x)

                            val cFoodJsonObject: JSONObject = cFavorite.getJSONObject("food")

                            val sizesArrayList: ArrayList<FoodSize> = ArrayList();


                            val cFoodJsonSizes: JSONArray =
                                cFoodJsonObject.getJSONArray("sizes");
                            if (cFoodJsonSizes.length() != 0) {
                                for (s in 0 until cFoodJsonSizes.length()) {
                                    val cSize: JSONObject = cFoodJsonSizes.getJSONObject(s);
                                    sizesArrayList.add(FoodSize(cSize.getInt("id"),
                                        cSize.getString("size"),
                                        cSize.getInt("price")));
                                }
                            }

                            val foodPrice: String = cFoodJsonObject.getString("price");

                            val cFood: Food = Food(
                                cFoodJsonObject.getInt("id"),
                                null,
                                cFoodJsonObject.getString("name"),
                                if (foodPrice != "null") foodPrice.toInt() else 0,
                                cFoodJsonObject.getString("description"),
                                cFoodJsonObject.getString("imageUrl"),
                                sizesArrayList,
                                true
                            );
                            favoritesFoodsArrayList.add(cFood);
                        }


                        mutableLiveDataFavourites.value = favoritesFoodsArrayList;
                    }


                },
                {
                    if (it is NetworkError || it is NoConnectionError || it is TimeoutError)
                        FailToast.showFailToast("Network error", context = fContext)
                    else FailToast.showFailToast("Something wrong !  ", fContext)
                });

        fetchFavoritesFoodsRequest.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        val queue: RequestQueue = Volley.newRequestQueue(fContext);
        queue.add(fetchFavoritesFoodsRequest);
        return mutableLiveDataFavourites;
    }


    // this function return Int {0 ==> error , 1 ==>liked , -1 ==> disliked}
    public fun likeFood(user_Id: Int, food_Id: Int): MutableLiveData<Int> {
        val likeStatus = MutableLiveData<Int>(0);

        val likeFood = object : StringRequest(Method.POST, API_URLS.FAVORITES_URL, {
            val jsonResponse = JSONObject(it);
            if (jsonResponse.getBoolean("success")) {
                val stringlikeStatus = jsonResponse.getString("message")
                if (stringlikeStatus == "liked") {
                    likeStatus.value = 1;
                } else if (stringlikeStatus == "disliked") {
                    likeStatus.value = -1
                }

            }


        }, {}) {
            override fun getParams(): MutableMap<String, String>? {
                val params: MutableMap<String, String> = HashMap<String, String>();
                params["user_Id"] = user_Id.toString();
                params["food_Id"] = food_Id.toString();
                return params;
            }

        };

        likeFood.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        val queue = Volley.newRequestQueue(fContext);
        queue.add(likeFood)
        return likeStatus;
    }


}