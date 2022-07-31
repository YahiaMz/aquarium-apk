package com.aplication.aquaruim.repositories

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.aplication.aquaruim.utils.API_URLS
import com.aplication.aquaruim.views.customViews.FailToast
import org.json.JSONObject

class AuthRepository private constructor(val context: Context) {
    companion object {
        private var instance: AuthRepository? = null

        fun getInstance(context: Context): AuthRepository {
            if (instance == null) {
                this.instance = AuthRepository(context)
            }
            return this.instance!!;
        }

    }


    public fun login(phoneNumber: String, password: String): MutableLiveData<Boolean> {
        val loginWithSuccess: MutableLiveData<Boolean> = MutableLiveData(false);
        val loginRequest = object : StringRequest(Method.POST, API_URLS.LOGIN_URL, {
            val JSONResponse = JSONObject(it);
            if (JSONResponse.getBoolean("success")) {
                loginWithSuccess.value = true;
            } else {
                FailToast.showFailToast("wrong phone number or password !" , context);
            }
        }, {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT)
                .show();
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>();
                params["phoneNumber"] = phoneNumber;
                params["password"] = password;
                return params;
            }
        };
        val authQueue = Volley.newRequestQueue(context);
        authQueue.add(loginRequest);

        return loginWithSuccess;
    }

}