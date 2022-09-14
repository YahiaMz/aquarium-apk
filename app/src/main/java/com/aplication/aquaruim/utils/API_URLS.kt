package com.aplication.aquaruim.utils

class API_URLS {
    companion object {

        //val MAIN_URL : String = "http://192.168.1.42:5000/";
        const val MAIN_URL: String = "https://pizzeria-aquarium-backend-production.up.railway.app/";


        const val LOGIN_URL = MAIN_URL + "users/login";
        const val SIGNUP_URL = MAIN_URL + "users/sign-up";
        const val USER_PROFILE_IMAGE = MAIN_URL + "users/images/"
        const val CATEGORIES_URL = MAIN_URL + "categories/"
        const val CATEGORIES_IMAGES_URL: String = CATEGORIES_URL + "images/"

        // ------------------------ FOODS ---------------------------//
        const val FOODS_URL: String = MAIN_URL + "foods/";
        const val FOOD_IMAGES_URL: String = FOODS_URL + "images/";
        // ------------------------END FOODS APIS --------------------//

        // ------------------------ CART APIS ------------------------//
        public const val USER_CART_ITEMS = MAIN_URL + "cart/"
        public const val ADD_CART_ITEM = MAIN_URL + "cart/"
        public const val CHANGE_CART_ITEM_SIZE = MAIN_URL + "cart/"

        // ------------------------ CART APIS ------------------------//


        const val PLACE_ORDER_URL: String = MAIN_URL + "orders"
        const val FETCH_USER_ORDERS: String = MAIN_URL + "orders/"


        val FETCH_SLIDES_URL = MAIN_URL + "slides/"
        val SLIDES_IMAGES_URL = FETCH_SLIDES_URL + "images/"

        // ----------------------- FAVOURITES ------------------------- //
        val FAVORITES_URL: String = MAIN_URL + "favourites/";

        val FETCH_AREAS = MAIN_URL + "areas/";
    }


}