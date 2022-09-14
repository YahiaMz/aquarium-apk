package com.aplication.aquaruim.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.aplication.aquaruim.adapters.OrderItemAdapter
import com.aplication.aquaruim.models.FoodSize
import com.aplication.aquaruim.models.Order
import com.aplication.aquaruim.models.OrderItem
import com.aplication.aquaruim.network.Responses.FetchOrdersResponse
import com.aplication.aquaruim.utils.API_URLS
import com.aplication.aquaruim.utils.MResponseStatus
import com.aplication.aquaruim.views.customViews.FailToast
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class OrdersRepository private constructor(val context: Context) {

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
        zone: String,
        address: String,
        orderPhoneNumber: String,
        token: String,
    ): MutableLiveData<Boolean> {
        val isOrderPlacedWithSuccess: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false);

        val placeOrderRequest: StringRequest =
            object : StringRequest(Method.POST, API_URLS.PLACE_ORDER_URL, {
                val jsonResponse: JSONObject = JSONObject(it);
                if (jsonResponse.getBoolean("success")) {
                    isOrderPlacedWithSuccess.value = true;
                }

            }, {
                if (it is NetworkError || it is NoConnectionError || it is TimeoutError) {
                    FailToast.showFailToast("Network err", context)

                } else {
                    FailToast.showFailToast("Something wrong ! , please re-run the app", context)
                }
            }) {

                override fun getHeaders(): MutableMap<String, String> {
                    val headers: MutableMap<String, String> = HashMap<String, String>()
                    headers["Authorization"] = "Bearer $token";
                    return headers
                }

                override fun getParams(): MutableMap<String, String>? {
                    val params: MutableMap<String, String> = HashMap<String, String>();
                    params["user_Id"] = userId.toString();
                    params["orderPhoneNumber"] = orderPhoneNumber;
                    params["address"] = address;
                    params["area"] = zone;

                    return params;
                }
            }

        placeOrderRequest.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        val mQueue = Volley.newRequestQueue(context);
        mQueue.add(placeOrderRequest);
        return isOrderPlacedWithSuccess;
    }


    public fun fetchOrdersOfUser(
        userId: Int,
        token: String?,
    ): MutableLiveData<FetchOrdersResponse> {

        val mutableOrdersResponse = MutableLiveData<FetchOrdersResponse>(null)
        val fetchUserOrderRequest = object :
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


                                val foodSizeJSONString: String =
                                    cJsonOrderItem.getString("foodSize");
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
                            val createdAtTimestamp = jsonOrder.getString("created_at");




                           val formattedTime = DateTimeFormatter
                                .ofPattern("yyyy-MM-dd HH:mm:ss")
                                .withZone(ZoneOffset.UTC)
                                .format(Instant.parse(createdAtTimestamp.toString()).plusSeconds(3600))


                            val cOrder = Order(
                                jsonOrder.getInt("id"),
                                jsonOrder.getString("area"),
                                jsonOrder.getInt("status"),
                                orderItemAdapter,
                                jsonOrder.getString("address"),
                                jsonOrder.getInt("totalPrice"),
                                formattedTime,
                                jsonOrder.getString("phoneNumber"),
                                jsonOrder.getBoolean("isReceived")
                            );

                            orders.add(cOrder);
                        }
                        mutableOrdersResponse.value =
                            FetchOrdersResponse(MResponseStatus.SUCCESS_RESPONSE, orders);
                    }
                }, {
                    if (it is NetworkError || it is NoConnectionError || it is TimeoutError) {
                        FailToast.showFailToast("Network err", context)
                        mutableOrdersResponse.value =
                            FetchOrdersResponse(MResponseStatus.NO_INTERNET, null)
                    } else {
                        FailToast.showFailToast("Something wrong !", context)
                        mutableOrdersResponse.value =
                            FetchOrdersResponse(MResponseStatus.SOMETHING_WRONG, null)
                    }
                }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers: MutableMap<String, String> = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token";
                return headers
            }
        };


        val mQueue = Volley.newRequestQueue(context);
        mQueue.add(fetchUserOrderRequest)
        return mutableOrdersResponse;
    }


    public fun ReceiveOrder(order_Id: Int, token: String): MutableLiveData<Boolean> {
        val isSuccess: MutableLiveData<Boolean> = MutableLiveData(null);

        val addToCartRequest: StringRequest = object : StringRequest(Request.Method.PUT,
            API_URLS.FETCH_USER_ORDERS + order_Id.toString() + "/received",
            {

                val JsonResponse: JSONObject = JSONObject(it);
                isSuccess.value = JsonResponse.getBoolean("success")

            },
            {
                if (it is NetworkError || it is NoConnectionError || it is TimeoutError) {
                    FailToast.showFailToast("Network err", context)
                    isSuccess.value = false

                } else {
                    FailToast.showFailToast("Something wrong !", context)
                    isSuccess.value = false

                }
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers: MutableMap<String, String> = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token";
                return headers
            }
        }

        val mQueue = Volley.newRequestQueue(context);
        mQueue.add(addToCartRequest);

        return isSuccess;
    }


}