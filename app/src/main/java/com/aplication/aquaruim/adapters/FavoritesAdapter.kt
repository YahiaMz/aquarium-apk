package com.aplication.aquaruim.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.aplication.aquaruim.databinding.FavouritesFoodItemContainerBinding
import com.aplication.aquaruim.databinding.FoodItemContainerBinding
import com.aplication.aquaruim.interfaces.IonAddItemToCart
import com.aplication.aquaruim.models.Food
import com.aplication.aquaruim.utils.API_URLS
import com.squareup.picasso.Picasso

class FavoritesAdapter public constructor(
    var favoriteFoods: ArrayList<Food>,
    public val onAddBtnClicked: IonAddItemToCart,
) : RecyclerView.Adapter<FavoritesAdapter.FavoriteFoodsViewHolder>() {

    var layoutInflater: LayoutInflater? = null;
    lateinit var favouriteFoodItemContainerBinding: FavouritesFoodItemContainerBinding;

    public class FavoriteFoodsViewHolder public constructor(var itemBinding: FavouritesFoodItemContainerBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        public fun bind(food: Food) {
            itemBinding.cFood = food;
            Picasso.get().load(API_URLS.FOOD_IMAGES_URL + food.imageUrl)
                .into(itemBinding.favouriteFoodItemImageView);
            this.itemBinding.addOneToCartBtnFromFavourites.setOnClickListener {

            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteFoodsViewHolder {
        if (layoutInflater == null) {
            this.layoutInflater = LayoutInflater.from(parent.context);
        }

        this.favouriteFoodItemContainerBinding =
            FavouritesFoodItemContainerBinding.inflate(layoutInflater!!);
        val layoutParams: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 16, 0, 16);
        this.favouriteFoodItemContainerBinding.root.layoutParams = layoutParams;

        return FavoriteFoodsViewHolder(this.favouriteFoodItemContainerBinding);
    }

    override fun onBindViewHolder(holder: FavoriteFoodsViewHolder, position: Int) {
        val food = favoriteFoods[position];
        holder.bind(food)
        holder.itemBinding.addOneToCartBtnFromFavourites.setOnClickListener {
            this.onAddBtnClicked.onAddItemCartBtnClicked(food = food);
        }
    }

    override fun getItemCount(): Int {
        return this.favoriteFoods.size
    }


}