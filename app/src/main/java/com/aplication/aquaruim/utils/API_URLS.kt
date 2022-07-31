package com.aplication.aquaruim.utils

class API_URLS {
    companion object {
        //https://pizzeria-aquarium-backend-production.up.railway.app/
        //http://192.168.1.39:5000/

        private const val MAIN_URL : String = "http://192.168.1.39:5000/";

        const val LOGIN_URL = MAIN_URL + "users/login";

        public const val CATEGORIES_URL = MAIN_URL + "categories/"
        public const val CATEGORIES_IMAGES_URL = CATEGORIES_URL + "images/"

        // ------------------------ FOODS ---------------------------//
        public const val FOODS_URL: String = MAIN_URL + "foods/";
        public const val FOOD_IMAGES_URL: String = FOODS_URL + "images/";
        // ------------------------END FOODS APIS --------------------//

        // ------------------------ CART APIS ------------------------//
         public const val USER_CART_ITEMS = MAIN_URL + "cart/"
        public const val ADD_CART_ITEM = MAIN_URL + "cart/"
        public const val CHANGE_CART_ITEM_SIZE = MAIN_URL + "cart/"

        // ------------------------ CART APIS ------------------------//


        val PLACE_ORDER_URL: String = MAIN_URL + "orders"
        val FETCH_USER_ORDERS : String = MAIN_URL + "orders/"


        // ----------------------- FAVOURITES ------------------------- //
        const val FAVORITES_URL : String = MAIN_URL + "favourites/";
    }


}