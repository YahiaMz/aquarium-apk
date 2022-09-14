package com.aplication.aquaruim.repositories

import android.accounts.NetworkErrorException
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.aplication.aquaruim.models.Category
import com.aplication.aquaruim.models.Food
import com.aplication.aquaruim.models.FoodSize
import com.aplication.aquaruim.network.Responses.FetchCategoriesResponse
import com.aplication.aquaruim.network.Responses.FetchFoodsResponse
import com.aplication.aquaruim.utils.API_URLS
import com.aplication.aquaruim.utils.MResponseStatus
import org.json.JSONArray
import org.json.JSONObject

class HomeRepository {

    var context: Context;

    private constructor(context: Context) {
        this.context = context
    }

    companion object {
        private var instance: HomeRepository? = null;
        public fun getInstance(context: Context): HomeRepository {
            if (this.instance == null) {
                this.instance = HomeRepository(context);
            }
            return instance as HomeRepository;
        }
    }

    public fun fetchFoodsToUser(user_Id: Int): MutableLiveData<FetchFoodsResponse> {

        //Toast.makeText(context , "fetching data ", Toast.LENGTH_SHORT).show()

        val mutableLiveDataFoods: MutableLiveData<FetchFoodsResponse> = MutableLiveData(null);
        val fetchFoodsRequest: StringRequest =
            StringRequest(Request.Method.GET, API_URLS.FOODS_URL + user_Id.toString(),
                {

                        val jsonResponse: JSONObject = JSONObject(it);
                        if (jsonResponse.getBoolean("success")) {
                            val foodsArrayList: ArrayList<Food> = ArrayList<Food>();

                            val foodsJSONObjects: JSONArray = jsonResponse.getJSONArray("message");
                            for (x in 0 until foodsJSONObjects.length()) {
                                val cFoodJsonObject: JSONObject = foodsJSONObjects.getJSONObject(x)

                                //  Toast.makeText(context , "jsonFood $cFoodJsonObject", Toast.LENGTH_SHORT).show();
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
                                    cFoodJsonObject.getJSONObject("Category").getInt("id"),
                                    cFoodJsonObject.getString("name"),
                                    if (foodPrice != "null") foodPrice.toInt() else 0,
                                    cFoodJsonObject.getString("description"),
                                    cFoodJsonObject.getString("imageUrl"),
                                    sizesArrayList,
                                    cFoodJsonObject.getBoolean("isLiked")
                                );
                                foodsArrayList.add(cFood);
                            }


                            mutableLiveDataFoods.value = FetchFoodsResponse(status = MResponseStatus.SUCCESS_RESPONSE, foodsArrayList);
                        }




                },
                {
                    if(it is NetworkError || it is NoConnectionError || it is TimeoutError)
                        mutableLiveDataFoods.value = FetchFoodsResponse(status = MResponseStatus.NO_INTERNET, null) ;
                    else mutableLiveDataFoods.value = FetchFoodsResponse(status = MResponseStatus.SOMETHING_WRONG , null)

                });

        val queue: RequestQueue = Volley.newRequestQueue(context);
        queue.add(fetchFoodsRequest);
        return mutableLiveDataFoods;
    }


    public fun fetchCategories(): MutableLiveData<FetchCategoriesResponse> {
        var mutableCategories = MutableLiveData<FetchCategoriesResponse>(null);

        val fetchCategoriesRequest: StringRequest =
            StringRequest(Request.Method.GET, API_URLS.CATEGORIES_URL, {
                val JSONResponse = JSONObject(it);
                if (JSONResponse.getBoolean("success")) {
                    val categories: ArrayList<Category> = ArrayList<Category>();
                    val JSONCategoriesArray = JSONResponse.getJSONArray("message")
                    for (x in 0 until JSONCategoriesArray.length()) {
                        val cJSONCategory = JSONCategoriesArray.getJSONObject(x);

                        val category = Category(cJSONCategory.getInt("id"),
                            cJSONCategory.getString("name"),
                            cJSONCategory.getString("imageUrl"));
                        categories.add(category);

                    }
                    mutableCategories.value = FetchCategoriesResponse(MResponseStatus.SUCCESS_RESPONSE , categories);
                }


            }, {
                if(it is NetworkError || it is NoConnectionError || it is TimeoutError)
                    mutableCategories.value = FetchCategoriesResponse(status = MResponseStatus.NO_INTERNET, null) ;
                else mutableCategories.value = FetchCategoriesResponse(status = MResponseStatus.SOMETHING_WRONG , null)
            }
            );

        val mQueue = Volley.newRequestQueue(context);
        mQueue.add(fetchCategoriesRequest);
        return mutableCategories;
    }

    fun fetchSlides(): MutableLiveData<ArrayList<String>> {
        val mutableSlideUrls = MutableLiveData<ArrayList<String>>(null)
        val fetchSlidesRequest =
            StringRequest(Request.Method.GET, API_URLS.FETCH_SLIDES_URL, {
                val responseObject = JSONObject(it)
                if(responseObject.getBoolean("success")) {
                    val slidesUrls = ArrayList<String>()
                    val jsonSlidesUrls = responseObject.getJSONArray("message")
                    for(x in 0 until jsonSlidesUrls.length() ){
                        slidesUrls.add(jsonSlidesUrls.getJSONObject(x).getString("imageUrl"))
                    }

                    mutableSlideUrls.value = slidesUrls;


                }


            }, {

            })

        Volley.newRequestQueue(context).add(fetchSlidesRequest);
        return mutableSlideUrls;

    }


}