package com.aplication.aquaruim.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.aplication.aquaruim.R
import com.aplication.aquaruim.databinding.FoodItemContainerBinding
import com.aplication.aquaruim.interfaces.IlikeFood
import com.aplication.aquaruim.interfaces.IonAddItemToCart
import com.aplication.aquaruim.interfaces.IonFoodItemClicked
import com.aplication.aquaruim.models.Food

class FoodsAdapter public constructor(
    var foods: ArrayList<Food>,
    val onAddBtnClicked: IonAddItemToCart,
    val ionFoodItemClicked: IonFoodItemClicked,
    val ilikeFood: IlikeFood,
) : RecyclerView.Adapter<FoodsAdapter.FoodItemHolder>() {

    var layoutInflater: LayoutInflater? = null;
    lateinit var foodItemContainerBinding: FoodItemContainerBinding;

    public class FoodItemHolder public constructor(var itemBinding: FoodItemContainerBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        public fun bind(food: Food) {
            itemBinding.cFood = food;
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemHolder {
        if (layoutInflater == null) {
            this.layoutInflater = LayoutInflater.from(parent.context);
        }

        this.foodItemContainerBinding = FoodItemContainerBinding.inflate(layoutInflater!!);
        return FoodItemHolder(this.foodItemContainerBinding);

    }

    override fun onBindViewHolder(holder: FoodItemHolder, position: Int) {
        val food = foods[position];
        holder.bind(food)
        holder.itemBinding.addOneToCartBtnFromHome.setOnClickListener {
            if (food.isAddingItem == false) {
                food.isAddingItem = true;
                notifyItemChanged(position);
                this.onAddBtnClicked.onAddItemCartBtnClicked(food = food, position);
            }

        }
        val animation =
            AnimationUtils.loadAnimation(holder.itemBinding.root.context, R.anim.slide_from_bottom);

        if (food.isAddingItem == false)
            holder.itemBinding.root.startAnimation(animation);
        holder.itemBinding.root.setOnClickListener {
            ionFoodItemClicked.onFoodItemClicked(food);
        }
        holder.itemBinding.likeFoodButton.setOnClickListener {
            this.ilikeFood.likeFood(food.id, position);
        }

    }

    override fun getItemCount(): Int {
        return this.foods.size
    }


}