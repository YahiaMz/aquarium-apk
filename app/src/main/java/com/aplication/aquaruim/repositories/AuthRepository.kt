package com.aplication.aquaruim.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkError
import com.android.volley.NoConnectionError
import com.android.volley.TimeoutError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.aplication.aquaruim.models.UpdateUserModel
import com.aplication.aquaruim.network.ApiRepsonses.UpdateUserResponse
import com.aplication.aquaruim.network.UserApiService
import com.aplication.aquaruim.utils.API_URLS
import com.aplication.aquaruim.utils.RetrofitBuilder
import com.aplication.aquaruim.views.customViews.FailToast
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository private constructor(val context: Context) {
    val mApiClient = RetrofitBuilder.getInstance().create(UserApiService::class.java);

    companion object {
        private var instance: AuthRepository? = null
        private lateinit var sharedPreferences: SharedPreferences;
        fun getInstance(context: Context): AuthRepository {
            if (instance == null) {
                this.instance = AuthRepository(context)
            }
            return this.instance!!;
        }

    }


    public fun login(phoneNumber: String, password: String): MutableLiveData<Boolean> {
        val loginWithSuccess: MutableLiveData<Boolean> = MutableLiveData(null);
        val loginRequest = object : StringRequest(Method.POST, API_URLS.LOGIN_URL, {
            val JSONResponse = JSONObject(it);
            Log.d("LOGIN", it.toString())
            if (JSONResponse.getBoolean("success")) {

                var userJSONInfo = JSONResponse.getJSONObject("message");

                val userId = userJSONInfo.getInt("id")
                val userName = userJSONInfo.getString("name")
                val userPhoneNumber = userJSONInfo.getString("phoneNumber")
                val imageProfileUrl = userJSONInfo.getString("imageProfileUrl")
                val lastName = userJSONInfo.getString("lastName");
                val token = userJSONInfo.getString("token")

                sharedPreferences = context.getSharedPreferences("USER", Context.MODE_PRIVATE);


                val spEditor = sharedPreferences.edit();
                spEditor.putBoolean("is_login", true);
                spEditor.putInt("user_id", userId);
                spEditor.putString("phone_number", userPhoneNumber)
                spEditor.putString("lastName", lastName)
                spEditor.putString("name", userName);
                spEditor.putString("imageProfileUrl", imageProfileUrl);
                spEditor.putString("token", token);
                spEditor.putString("password", password)


                spEditor.apply();
                loginWithSuccess.value = true;
            } else {
                loginWithSuccess.value = false
                FailToast.showFailToast("wrong phone number or password !", context);
            }
        }, {
            loginWithSuccess.value = false
            if (it is NetworkError || it is NoConnectionError || it is TimeoutError)
                FailToast.showFailToast("Network err", context)
            else FailToast.showFailToast("Something wrong !", context)
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>();
                params["phoneNumber"] = phoneNumber;
                params["password"] = password;
                return params;
            }
        };

        loginRequest.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        val authQueue = Volley.newRequestQueue(context);
        authQueue.add(loginRequest);

        return loginWithSuccess;
    }


    public fun signUp(phoneNumber: String, password: String): MutableLiveData<Int> {
        val loginWithSuccess: MutableLiveData<Int> = MutableLiveData(null);
        val loginRequest = object : StringRequest(Method.POST, API_URLS.SIGNUP_URL, {
            val JSONResponse = JSONObject(it);

            if (JSONResponse.getBoolean("success")) {

                var userJSONInfo = JSONResponse.getJSONObject("message");

                val userId = userJSONInfo.getInt("id")
                val userPhoneNumber = userJSONInfo.getString("phoneNumber")
                val token = userJSONInfo.getString("token")

                sharedPreferences = context.getSharedPreferences("USER", Context.MODE_PRIVATE);


                val spEditor = sharedPreferences.edit();
                spEditor.putBoolean("is_login", true);
                spEditor.putInt("user_id", userId);
                spEditor.putString("phone_number", userPhoneNumber)
                spEditor.putString("token", token);
                spEditor.putString("password", password)

                spEditor.apply();

                loginWithSuccess.value = 1;
            } else {
                loginWithSuccess.value = 0;
                if (JSONResponse.getString("message") == "PHONE NUMBER EXIST") FailToast.showFailToast(
                    context = context,
                    message = "This phone number already exist!",
                )
                else FailToast.showFailToast(
                    context = context,
                    message = "Something wrong !",
                )
            }
        }, {
            loginWithSuccess.value = 0;

        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>();
                params["phoneNumber"] = phoneNumber;
                params["password"] = password;
                return params;
            }
        };

        loginRequest.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        val authQueue = Volley.newRequestQueue(context);
        authQueue.add(loginRequest);

        return loginWithSuccess;
    }


    fun updateUser(
        updateUserModel: UpdateUserModel,
    ): MutableLiveData<Int> {
        val updatedUser = MutableLiveData<Int>(null)


        val mToken =
            context.getSharedPreferences("USER", Context.MODE_PRIVATE).getString("token", "null");

        this.mApiClient.updateUser(
            "Bearer $mToken",
            updateUserModel.user_Id.toString(),
            updateUserModel.name,
            updateUserModel.lastName,
            updateUserModel.phoneNumber, updateUserModel.password, updateUserModel.profileImage
        ).enqueue(object : Callback<UpdateUserResponse> {
            override fun onResponse(
                call: Call<UpdateUserResponse>,
                response: Response<UpdateUserResponse>,
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.success == true) {

                        val message = response.body()?.message;
                        if (message != "UPDATED") {
                            val spEditor =
                                context.getSharedPreferences("USER", Context.MODE_PRIVATE)
                                    .edit();
                            spEditor.putString("imageProfileUrl", message);
                            spEditor.apply();
                        }

                        updatedUser.value = 1;

                    } else {

                        FailToast.showFailToast(response.body()?.message.toString(), context);
                        updatedUser.value = 0
                    }
                } else {
                    FailToast.showFailToast("Something wrong , not updated", context);
                }
            }

            override fun onFailure(call: Call<UpdateUserResponse>, t: Throwable) {
                updatedUser.value = -1
                FailToast.showFailToast("Not updated , something wrong", context);
            }

        })


        return updatedUser;
    }


}