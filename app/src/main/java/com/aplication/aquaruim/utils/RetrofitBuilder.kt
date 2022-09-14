package com .aplication.aquaruim.utils

import com.aplication.aquaruim.utils.API_URLS
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitBuilder private constructor() {
    companion object {
        private var instance: Retrofit? = null;
        public fun getInstance(): Retrofit {
            if (instance == null) {
                val mGsonConverterFactory = GsonBuilder().setLenient().create();

                instance = Retrofit.Builder().baseUrl(API_URLS.MAIN_URL)
                    .addConverterFactory(GsonConverterFactory.create(mGsonConverterFactory))
                    .build();
            }
            return this.instance!!;
        }
    }
}