package com.aplication.aquaruim.repositories

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.aplication.aquaruim.models.CartItem
import com.aplication.aquaruim.models.Food
import com.aplication.aquaruim.models.FoodSize
import com.aplication.aquaruim.network.Responses.FetchCartItemsResponse
import com.aplication.aquaruim.utils.API_URLS
import com.aplication.aquaruim.utils.MResponseStatus
import com.aplication.aquaruim.views.customViews.FailToast
import org.json.JSONArray
import org.json.JSONObject


class CartItemRepository {

    val mContext: Context;


    private constructor(context: Context) {
        this.mContext = context;
    }

    companion object {
        private var instance: CartItemRepository? = null;

        public fun getInstance(context: Context): CartItemRepository {
            if (this.instance == null) {
                instance = CartItemRepository(context);
            }
            return instance!!;
        }
    }

    public var mutableCartItems: MutableLiveData<FetchCartItemsResponse> = MutableLiveData(null);

    public fun fetchCartItemsOfUser(
        user_Id: Int,
        token: String,
    ): MutableLiveData<FetchCartItemsResponse> {

        val fMutableCartItems =
            MutableLiveData<FetchCartItemsResponse>(null);

        val fetchUserCartItems: StringRequest = object :
            StringRequest(Request.Method.GET, API_URLS.USER_CART_ITEMS + user_Id, {

                var JSONResponse: JSONObject = JSONObject(it);

                if (JSONResponse.getBoolean("success")) {

                    //        Toast.makeText(mContext, "fetching cart items", Toast.LENGTH_SHORT).show();

                    val cartItemsArrayList: ArrayList<CartItem> = ArrayList<CartItem>()
                    val cartItemsJSONArray: JSONArray = JSONResponse.getJSONArray("message");

                    for (x in 0 until cartItemsJSONArray.length()) {
                        val cJSONItem = cartItemsJSONArray.getJSONObject(x);
                        val cJSONFood = cJSONItem.getJSONObject("food");

                        val cFoodJsonSizes: JSONArray = cJSONFood.getJSONArray("sizes");
                        val sizesArrayList: ArrayList<FoodSize> = ArrayList();


                        if (cFoodJsonSizes.length() != 0) {
                            for (s in 0 until cFoodJsonSizes.length()) {
                                val cSize: JSONObject = cFoodJsonSizes.getJSONObject(s);

                                sizesArrayList.add(FoodSize(cSize.getInt("id"),
                                    cSize.getString("size"),
                                    cSize.getInt("price")));
                            }
                        }

                        val foodPrice: String = cJSONFood.getString("price");

                        val foodSizeJSONString: String = cJSONItem.getString("foodSize");
                        var foodSize: FoodSize? = null;
                        if (foodSizeJSONString != "null") {

                            val foodSizeJSON = cJSONItem.getJSONObject("foodSize")
                            foodSize = FoodSize(
                                foodSizeJSON.getInt("id"),
                                foodSizeJSON.getString("size"),
                                foodSizeJSON.getInt("price")
                            )
                        }

                        val cFood = Food(
                            cJSONFood.getInt("id"),
                            null,
                            cJSONFood.getString("name"),
                            if (foodPrice != "null") foodPrice.toInt() else 0,
                            "",
                            cJSONFood.getString("imageUrl"), sizesArrayList,
                            null);
                        val cCartItem: CartItem = CartItem(cJSONItem.getInt("id"),
                            cFood,
                            foodSize,
                            cJSONItem.getInt("quantity")
                        )
                        ;
                        cartItemsArrayList.add(cCartItem);
                    }
                    val mResponse = FetchCartItemsResponse(MResponseStatus.SUCCESS_RESPONSE,
                        cartItemsArrayList);
                    fMutableCartItems.value = mResponse
                    this.mutableCartItems.value = mResponse;
                }
            }, {

                if (it is NetworkError || it is NoConnectionError || it is TimeoutError) {

                    val fMcI = FetchCartItemsResponse(MResponseStatus.NO_INTERNET, null)

                    fMutableCartItems.value = fMcI
                    this.mutableCartItems.value = fMcI;
                } else {
                    fMutableCartItems.value =
                        FetchCartItemsResponse(status = MResponseStatus.SOMETHING_WRONG, null)
                    this.mutableCartItems = fMutableCartItems;

                }

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers: MutableMap<String, String> = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token";
                return headers
            }
        };

        val queue = Volley.newRequestQueue(mContext);
        queue.add(fetchUserCartItems);
        return fMutableCartItems;
    }


    public fun addToCart(
        user_Id: Int,
        food_Id: Int,
        foodSize_Id: Int,
        quantity: Int,
        token: String,
    ): MutableLiveData<Boolean> {

        Log.d("ADD_ITEM", "Repository")


        val isSuccess: MutableLiveData<Boolean> = MutableLiveData(null);

        val addToCartRequest: StringRequest =
            object : StringRequest(Request.Method.POST, API_URLS.ADD_CART_ITEM, {
                val JsonResponse: JSONObject = JSONObject(it);
                if (JsonResponse.getBoolean("success")) {
                    isSuccess.value = true;
                } else {
                    isSuccess.value = false
                    val message = JsonResponse.getString("message");
                    FailToast.showFailToast(message, mContext)


                }


            }, {
                isSuccess.value = false

                if (it is NetworkError || it is NoConnectionError || it is TimeoutError)
                    FailToast.showFailToast("Network err", mContext)
                else FailToast.showFailToast("Something wrong , item not added ", mContext)
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers: MutableMap<String, String> = HashMap<String, String>()
                    headers["Authorization"] = "Bearer $token";
                    return headers
                }

                override fun getParams(): MutableMap<String, String>? {
                    val params: MutableMap<String, String> = HashMap();
                    params["food_Id"] = food_Id.toString();
                    params["user_Id"] = user_Id.toString();
                    params["quantity"] = quantity.toString();
                    if (foodSize_Id != -1) {
                        params["size_Id"] = foodSize_Id.toString();
                    }
                    return params;
                }
            };


        addToCartRequest.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        Volley.newRequestQueue(mContext).add(addToCartRequest);
        //mQueue.add(addToCartRequest);

        return isSuccess;
    }

    public fun removeFromCart(
        cartItem_Id: Int,
        token: String,
    ): MutableLiveData<Boolean> {
        val isSuccess: MutableLiveData<Boolean> = MutableLiveData(false);


        val addToCartRequest: StringRequest = object : StringRequest(Request.Method.DELETE,
            API_URLS.USER_CART_ITEMS + cartItem_Id.toString(),
            {


                val JsonResponse = JSONObject(it);
                if (JsonResponse.getBoolean("success")) {
                    isSuccess.value = true;
                } else {
                    val message = JsonResponse.getString("message");
                    FailToast.showFailToast(message, mContext)
                }


            },
            {
                if (it is NetworkError || it is NoConnectionError || it is TimeoutError)
                    FailToast.showFailToast("Network err", mContext)
                else FailToast.showFailToast("Something wrong , item not removed ", mContext)

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers: MutableMap<String, String> = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token";
                return headers
            }
        };


        val mQueue = Volley.newRequestQueue(mContext);
        mQueue.add(addToCartRequest);

        return isSuccess;
    }

    fun changeItemQuantity(
        cartItem_Id: Int,
        quantity: Int,
        token: String,
    ): MutableLiveData<Int> {
        val isSuccess: MutableLiveData<Int> = MutableLiveData(null);

        val changeCartQuantityRequest: StringRequest = object : StringRequest(Request.Method.PATCH,
            API_URLS.USER_CART_ITEMS + cartItem_Id.toString() + "/add/" + quantity,
            {


                val JsonResponse = JSONObject(it);
                if (JsonResponse.getBoolean("success")) {
                    val JSONMessage: JSONObject = JsonResponse.getJSONObject("message")
                    val newQuantity: Int = JSONMessage.getInt("quantity");
                    isSuccess.value = newQuantity;
                } else {
                    val message = JsonResponse.getString("message")
                    isSuccess.value = -1;
                    FailToast.showFailToast(message, context = mContext)
                }


            },
            {
                if (it is NetworkError || it is NoConnectionError || it is TimeoutError)
                    FailToast.showFailToast("Network error", context = mContext)
                else FailToast.showFailToast("Something wrong !  ", mContext)

                isSuccess.value = -1;

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers: MutableMap<String, String> = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token";
                return headers
            }
        };

        changeCartQuantityRequest.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        val mQueue = Volley.newRequestQueue(mContext);
        mQueue.add(changeCartQuantityRequest);

        return isSuccess;
    }

    public fun changeFoodCartItemSize(
        user_Id: Int,
        cartItem_Id: Int,
        newSize_Id: Int,
        token: String,
    ): MutableLiveData<FoodSize> {
        val newFoodSize: MutableLiveData<FoodSize> = MutableLiveData(null);

        val changeCartQuantityRequest: StringRequest = object : StringRequest(Request.Method.PATCH,
            API_URLS.CHANGE_CART_ITEM_SIZE + user_Id.toString(),
            {


                val JsonResponse = JSONObject(it);
                if (JsonResponse.getBoolean("success")) {
                    val foodSizeJSON = JsonResponse.getJSONObject("message");
                    val foodSize = FoodSize(
                        foodSizeJSON.getInt("id"),
                        foodSizeJSON.getString("size"),
                        foodSizeJSON.getInt("price")
                    )
                    newFoodSize.value = foodSize;
                } else {
                    Toast.makeText(mContext, it, Toast.LENGTH_SHORT).show();
                }

            },
            {
                if (it is NetworkError || it is NoConnectionError || it is TimeoutError)
                    FailToast.showFailToast("Network err", mContext)
                else FailToast.showFailToast("Something wrong ! , size not changed ", mContext)
            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers: MutableMap<String, String> = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token";
                return headers
            }

            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap<String, String>()
                params["cartItem_Id"] = cartItem_Id.toString()
                params["size_Id"] = newSize_Id.toString();

                return params;
            }
        };

        changeCartQuantityRequest.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        val mQueue = Volley.newRequestQueue(mContext);
        mQueue.add(changeCartQuantityRequest);
        return newFoodSize;
    }


    public fun fetchAreas(): MutableLiveData<ArrayList<String>> {
        val mutableAreas = MutableLiveData<ArrayList<String>>(null)

        val fetchAreasRequest = StringRequest(Request.Method.GET, API_URLS.FETCH_AREAS, {
            val jsonResponse = JSONObject(it)
            if (jsonResponse.getBoolean("success")) {
                val areasAl = ArrayList<String>();
                val jsonAreas = jsonResponse.getJSONArray("message")
                for (x in 0 until jsonAreas.length()) {
                    areasAl.add(jsonAreas.getJSONObject(x).getString("area"));
                }

                mutableAreas.value = areasAl;
            } else {
                val message = jsonResponse.getString("message");
                FailToast.showFailToast(message, mContext)
            }
        }, {
            if (it is NetworkError || it is NoConnectionError || it is TimeoutError)
                FailToast.showFailToast("Network err", mContext)
            else FailToast.showFailToast("Something wrong !", mContext)
        })


        Volley.newRequestQueue(mContext).add(fetchAreasRequest);
        return mutableAreas;
    }


}