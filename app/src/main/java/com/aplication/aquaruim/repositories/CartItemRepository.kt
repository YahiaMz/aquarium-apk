package com.aplication.aquaruim.repositories

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.aplication.aquaruim.models.CartItem
import com.aplication.aquaruim.models.Food
import com.aplication.aquaruim.models.FoodSize
import com.aplication.aquaruim.utils.API_URLS
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

    public var mutableCartItems : MutableLiveData<ArrayList<CartItem>> = MutableLiveData(null);

    public fun fetchCartItemsOfUser(user_Id: Int) : MutableLiveData<ArrayList<CartItem>>{

        val fMutableCartItems: MutableLiveData<ArrayList<CartItem>> =
            MutableLiveData<ArrayList<CartItem>>(null);

        val fetchUserCartItems: StringRequest =
            StringRequest(Request.Method.GET, API_URLS.USER_CART_ITEMS + user_Id, {

                try {
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

                            val cFood: Food = Food(
                                cJSONFood.getInt("id"),
                                null ,
                                cJSONFood.getString("name"),
                                if (foodPrice != "null") foodPrice.toInt() else 0,
                                "",
                                cJSONFood.getString("imageUrl"), sizesArrayList
                            ,
                            null);
                            val cCartItem: CartItem = CartItem(cJSONItem.getInt("id"),
                                cFood,
                                foodSize,
                                cJSONItem.getInt("quantity")
                            )
                            ;
                            cartItemsArrayList.add(cCartItem);
                        }

                        fMutableCartItems.value = cartItemsArrayList;
                        this.mutableCartItems.value = cartItemsArrayList;
                    }

                } catch (e: VolleyError) {
                    Toast.makeText(mContext, "Catch : " + e.message, Toast.LENGTH_SHORT).show();
                }


            }, {
                Toast.makeText(mContext, "Error : " + it.message, Toast.LENGTH_SHORT).show();
            });

        val queue = Volley.newRequestQueue(mContext);
        queue.add(fetchUserCartItems);
return fMutableCartItems;
    }


    public fun addToCart(
        user_Id: Int,
        food_Id: Int,
        foodSize_Id: Int,
        quantity: Int,
    ): MutableLiveData<Boolean> {
        val isSuccess: MutableLiveData<Boolean> = MutableLiveData(false);

        val addToCartRequest: StringRequest =
            object : StringRequest(Request.Method.POST, API_URLS.ADD_CART_ITEM, {
                try {
                    val JsonResponse: JSONObject = JSONObject(it);
                    if (JsonResponse.getBoolean("success")) {
                        isSuccess.value = true;
                    }
                } catch (err: VolleyError) {
                    Toast.makeText(mContext, "In catch : " + err.message, Toast.LENGTH_SHORT)
                        .show();
                }

            }, {
                Toast.makeText(mContext, "In Error : " + it.message, Toast.LENGTH_SHORT).show();
            }) {

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


        val mQueue = Volley.newRequestQueue(mContext);
        mQueue.add(addToCartRequest);

        return isSuccess;
    }

    public fun removeFromCart(cartItem_Id: Int): MutableLiveData<Boolean> {
        val isSuccess: MutableLiveData<Boolean> = MutableLiveData(false);

        val addToCartRequest: StringRequest = StringRequest(Request.Method.DELETE,
            API_URLS.USER_CART_ITEMS + cartItem_Id.toString(),
            {


                try {
                    val JsonResponse: JSONObject = JSONObject(it);
                    if (JsonResponse.getBoolean("success")) {
                        isSuccess.value = true;
                    }
                } catch (err: VolleyError) {
                    Toast.makeText(mContext, "In catch : " + err.message, Toast.LENGTH_SHORT)
                        .show();
                }

            },
            {
                Toast.makeText(mContext, "In Error : " + it.message, Toast.LENGTH_SHORT).show();
            });


        val mQueue = Volley.newRequestQueue(mContext);
        mQueue.add(addToCartRequest);

        return isSuccess;
    }


    public fun changeItemQuantity(cartItem_Id: Int , quantity: Int): MutableLiveData<Int> {
        val isSuccess: MutableLiveData<Int> = MutableLiveData(-1);

        val changeCartQuantityRequest: StringRequest = StringRequest(Request.Method.PATCH,
            API_URLS.USER_CART_ITEMS + cartItem_Id.toString() + "/add/" + quantity,
            {


                try {
                    val JsonResponse: JSONObject = JSONObject(it);
                    if (JsonResponse.getBoolean("success")) {
                        val JSONMessage :JSONObject = JsonResponse.getJSONObject("message")
                        val newQuantity : Int = JSONMessage.getInt("quantity");
                        isSuccess.value = newQuantity;
                    }
                } catch (err: VolleyError) {
                    Toast.makeText(mContext, "In catch : " + err.message, Toast.LENGTH_SHORT)
                        .show();
                }

            },
            {
                Toast.makeText(mContext, "In Error : " + it.message, Toast.LENGTH_SHORT).show();
            });


        val mQueue = Volley.newRequestQueue(mContext);
        mQueue.add(changeCartQuantityRequest);

        return isSuccess;
    }



    public fun changeFoodCartItemSize(user_Id: Int , cartItem_Id: Int , newSize_Id: Int): MutableLiveData<FoodSize> {
        val newFoodSize: MutableLiveData<FoodSize> = MutableLiveData(null);

        val changeCartQuantityRequest: StringRequest = object  : StringRequest(Request.Method.PATCH,
            API_URLS.CHANGE_CART_ITEM_SIZE + user_Id.toString(),
            {


                    val JsonResponse: JSONObject = JSONObject(it);
                    if (JsonResponse.getBoolean("success")) {
                        val foodSizeJSON = JsonResponse.getJSONObject("message");
                        val foodSize = FoodSize(
                            foodSizeJSON.getInt("id"),
                            foodSizeJSON.getString("size"),
                            foodSizeJSON.getInt("price")
                        )
                        newFoodSize.value = foodSize;
                    }else {
                        Toast.makeText(mContext , it , Toast.LENGTH_SHORT).show();
                    }

            },
            {
                Toast.makeText(mContext, "In Error : " + it.message, Toast.LENGTH_SHORT).show();
            }){
            override fun getParams(): MutableMap<String, String> {
                val params : MutableMap<String ,String> = HashMap<String ,String>()
                params["cartItem_Id"] = cartItem_Id.toString()
                params["size_Id"] = newSize_Id.toString();

                return params;
            }
        };


        val mQueue = Volley.newRequestQueue(mContext);
        mQueue.add(changeCartQuantityRequest);

        return newFoodSize;
    }


}