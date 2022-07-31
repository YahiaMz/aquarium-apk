package com.aplication.aquaruim.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.RecyclerView
import com.aplication.aquaruim.databinding.CartItemContainerBinding
import com.aplication.aquaruim.databinding.FoodItemContainerBinding
import com.aplication.aquaruim.interfaces.IChangeCartFoodSize
import com.aplication.aquaruim.interfaces.IChangeCartItemQuantity
import com.aplication.aquaruim.interfaces.IonRemoveItemFromCart
import com.aplication.aquaruim.models.CartItem
import com.aplication.aquaruim.models.Food
import com.aplication.aquaruim.models.FoodSize
import com.aplication.aquaruim.utils.API_URLS
import com.squareup.picasso.Picasso

class CartAdapter constructor(var cartItems : ArrayList<CartItem> , val IonRemoveFromCart : IonRemoveItemFromCart , val iChangeCartItemQuantity: IChangeCartItemQuantity , val IChangeCartFoodSize : IChangeCartFoodSize) : RecyclerView.Adapter<CartAdapter.CartItemViewHolder>()  {

    lateinit var cartItemContainerBinding: CartItemContainerBinding;
    var layoutInflater : LayoutInflater?= null;
    public  class  CartItemViewHolder public  constructor(var itemBinding : CartItemContainerBinding ) : RecyclerView.ViewHolder (itemBinding.root) {
        public fun BindCartItem ( cartItem: CartItem ) {
              this.itemBinding.cartItem = cartItem;
              Picasso.get().load(API_URLS.FOOD_IMAGES_URL  + cartItem.food.imageUrl).into(this.itemBinding.cartItemImageView);

        }

        public fun getSizeId( foodSizes: ArrayList<FoodSize> , sizeName: String) : Int? {

            for (x in 0 until foodSizes.size) {
                if(foodSizes[x].sizeName.equals(sizeName)) {
                    return foodSizes[x].id;
                }
            }
            return  null;

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {

        if(layoutInflater == null)
        layoutInflater = LayoutInflater.from(parent.context);

        this.cartItemContainerBinding = CartItemContainerBinding.inflate(layoutInflater!! , parent ,false);

      return CartItemViewHolder(cartItemContainerBinding);

    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
          val cartItem : CartItem = this.cartItems[position];
          holder.BindCartItem(cartItem);
        // SETTING ROOT VIEW MARGIN {document about it why it does not work with databinding}
          val layoutParams : ConstraintLayout.LayoutParams =
            ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 16 , 0 , 16);
        holder.itemBinding.root.layoutParams =layoutParams;

        holder.itemBinding.removeImageButton.setOnClickListener{
            this.IonRemoveFromCart.onRemoveItemCartBtnClicked(cartItem.id ,cartItem.quantity, position);
        }



        holder.itemBinding.addOneToQuantity.setOnClickListener {
            this.iChangeCartItemQuantity.changeCartItemQuantity(cartItem , +1 , position);
        }
        holder.itemBinding.minusOneToQuantity.setOnClickListener {
            this.iChangeCartItemQuantity.changeCartItemQuantity(cartItem , -1 , position);
        }

        holder.itemBinding.smallSizeButton.setOnClickListener {
            val sizeId = holder.getSizeId(cartItem.food.sizes , "small");
            if(sizeId != null && sizeId != cartItem.foodSize?.id)
                this.IChangeCartFoodSize.changeFoodInCartSize( cartItem.id , sizeId , position )
        }

        holder.itemBinding.mediumSizeButton.setOnClickListener {
            val sizeId = holder.getSizeId(cartItem.food.sizes , "medium");
            if(sizeId != null && sizeId != cartItem.foodSize?.id)
                this.IChangeCartFoodSize.changeFoodInCartSize( cartItem.id , sizeId , position )
        }

        holder.itemBinding.largeSizeButton.setOnClickListener {
            val sizeId = holder.getSizeId(cartItem.food.sizes , "large");
            if(sizeId != null && sizeId != cartItem.foodSize?.id)
                this.IChangeCartFoodSize.changeFoodInCartSize( cartItem.id , sizeId , position )
        }



      }






    override fun getItemCount(): Int {
 return  this.cartItems.size;
    }

}