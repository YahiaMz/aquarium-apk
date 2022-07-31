package com.aplication.aquaruim.repositories

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.aplication.aquaruim.adapters.OrderItemAdapter
import com.aplication.aquaruim.models.FoodSize
import com.aplication.aquaruim.models.Order
import com.aplication.aquaruim.models.OrderItem
import com.aplication.aquaruim.utils.API_URLS
import org.json.JSONObject

class OrdersRepository private constructor(val context: Context) {
    public var mutableOrders : MutableLiveData<ArrayList<Order>> = MutableLiveData(null)
    companion object {
        private var ordersRepositoryInstance: OrdersRepository? = null;

        public fun getInstance(context: Context): OrdersRepository {
            if (this.ordersRepositoryInstance == null) {
                this.ordersRepositoryInstance = OrdersRepository(context);
            }
            return this.ordersRepositoryInstance!!;
        }
    }






    public fun placeOrder(
        userId: Int,
        address: String,
        orderPhoneNumber: String,
    ): MutableLiveData<Boolean> {
        val isOrderPlacedWithSuccess: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false);

        val placeOrderRequest: StringRequest =
            object : StringRequest(Method.POST, API_URLS.PLACE_ORDER_URL, {
                // Toast.makeText(context , it , Toast.LENGTH_SHORT).show();
                val jsonResponse: JSONObject = JSONObject(it);
                if (jsonResponse.getBoolean("success")) {
                    isOrderPlacedWithSuccess.value = true;
                }

            }, {
                Toast.makeText(context,
                    " Error :  " + it.message,
                    Toast.LENGTH_SHORT).show();
            }) {
                override fun getParams(): MutableMap<String, String>? {
                    val params: MutableMap<String, String> = HashMap<String, String>();
                    params["user_Id"] = userId.toString();
                    params["orderPhoneNumber"] = orderPhoneNumber;
                    params["address"] = address;

                    return params;
                }
            }


        val mQueue = Volley.newRequestQueue(context);
        mQueue.add(placeOrderRequest);
        return isOrderPlacedWithSuccess;
    }


    public fun fetchOrdersOfUser(userId: Int) {



        val fetchUserOrderRequest =
            StringRequest(Request.Method.GET, API_URLS.FETCH_USER_ORDERS + userId.toString(),
                {

                    val jsonResponse = JSONObject(it);
                    if (jsonResponse.getBoolean("success")) {

                        val orders = ArrayList<Order>();
                        val JSONMessage = jsonResponse.getJSONArray("message");
                        for (x in 0 until JSONMessage.length()) {
                            val jsonOrder = JSONMessage.getJSONObject(x);



                            val orderItemsArrayList = ArrayList<OrderItem>();
                            val jsonOrderItems = jsonOrder.getJSONArray("orderItems");

                            for (o in 0 until jsonOrderItems.length()) {
                                val cJsonOrderItem = jsonOrderItems.getJSONObject(o);



                                val foodSizeJSONString: String = cJsonOrderItem.getString("foodSize");
                                var foodSize: FoodSize? = null;
                                if (foodSizeJSONString != "null") {
                                    val foodSizeJSON = cJsonOrderItem.getJSONObject("foodSize")
                                    foodSize = FoodSize(
                                        foodSizeJSON.getInt("id"),
                                        foodSizeJSON.getString("size"),
                                        foodSizeJSON.getInt("price")
                                    )
                                }


                                val cJsonFood = cJsonOrderItem.getJSONObject("food");
                                val cOrderItem: OrderItem = OrderItem(
                                    cJsonOrderItem.getInt("id"),
                                    cJsonOrderItem.getInt("quantity"),
                                    cJsonFood.getString("imageUrl"),
                                    cJsonFood.getString("name"),
                                    foodSize
                                )

                                orderItemsArrayList.add(cOrderItem);

                            }


                            val orderItemAdapter = OrderItemAdapter(orderItemsArrayList);

                            val cOrder = Order(
                                jsonOrder.getInt("id"),
                                jsonOrder.getInt("status") ,
                                orderItemAdapter ,
                                jsonOrder.getString("address") ,
                                jsonOrder.getInt("totalPrice"),
                                jsonOrder.getString("created_at") ,
                                jsonOrder.getString("phoneNumber")
                                );

                            orders.add(cOrder);
                        }

                        mutableOrders.value = orders;
                        this.mutableOrders.value = orders;
                    }
                }, {

                });


        val mQueue = Volley.newRequestQueue(context);
        mQueue.add(fetchUserOrderRequest)

    }


}