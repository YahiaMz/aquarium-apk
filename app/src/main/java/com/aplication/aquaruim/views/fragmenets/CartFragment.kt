package com.aplication.aquaruim.views.fragmenets

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.aplication.aquaruim.adapters.CartAdapter
import com.aplication.aquaruim.databinding.FragmentCartBinding
import com.aplication.aquaruim.interfaces.IChangeCartFoodSize
import com.aplication.aquaruim.interfaces.IChangeCartItemQuantity
import com.aplication.aquaruim.interfaces.IonRemoveItemFromCart
import com.aplication.aquaruim.models.CartItem
import com.aplication.aquaruim.viewmodels.CartViewModel
import com.aplication.aquaruim.views.activities.CheckoutActivity


class CartFragment : Fragment() , IonRemoveItemFromCart  , IChangeCartItemQuantity , IChangeCartFoodSize{

   private lateinit var cartFragmentBinding : FragmentCartBinding ;
    val cartViewModel : CartViewModel by activityViewModels();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.cartFragmentBinding = FragmentCartBinding.inflate(inflater);
          watchCartItems();
          onCheckoutBtnClicked();
         return this.cartFragmentBinding.root;
    }


    private  fun  watchCartItems( ) {
        this.cartViewModel.mutableCartItems?.observe(CartFragment@this) {
            cartFragmentBinding.isLoading = true;
            if(it == null) {

            }else {
                if(it.isEmpty()) {
                    cartFragmentBinding.isCartEmpty = true;
                }else {

                    cartFragmentBinding.cartItemsAdapter = CartAdapter(it , this , this , this) ;
                    setTotalPrice(it);
                    cartFragmentBinding.isCartEmpty = it.isEmpty();

                }
                cartFragmentBinding.isLoading = false;

            }

        }
    }
    private fun onCheckoutBtnClicked( ) {
        this.cartFragmentBinding.checkoutBtn.setOnClickListener {
            val toCheckOutActivity  = Intent(activity, CheckoutActivity::class.java)
            startActivity(toCheckOutActivity)
        }
    }
    override fun onRemoveItemCartBtnClicked(cartItem_Id: Int , quantity : Int , pos : Int) {
        this.cartViewModel.removeFoodFromUserCart(cartItem_Id).observe(CartViewModel@this ) {
            if(it!= null && it ) {

                this.cartFragmentBinding.cartItemsAdapter?.cartItems?.removeAt(pos);
                this.cartFragmentBinding.cartItemsAdapter?.notifyItemRemoved(pos)
                this.cartFragmentBinding.cartItemsAdapter?.notifyItemRangeChanged(pos ,this.cartFragmentBinding.cartItemsAdapter?.cartItems!!.size )

                setTotalPrice(this.cartFragmentBinding.cartItemsAdapter?.cartItems!!);
                if(cartFragmentBinding.cartItemsAdapter?.cartItems!!.isEmpty()) {
                    cartFragmentBinding.isCartEmpty = true;
                }
                this.cartViewModel.cartCount.value = this.cartViewModel.cartCount.value?.minus(
                    quantity);

            }
        }
    }
    private fun setTotalPrice(arrayList: ArrayList<CartItem>)  {
        var sum = 0;
        for (x in 0 until arrayList.size) {
            val cartItem: CartItem = arrayList[x];

            if(cartItem.foodSize == null)
            sum += cartItem.quantity * cartItem.food.price!!;
            else
                sum += cartItem.foodSize!!.sizePrice * cartItem.quantity;
        }
        this.cartFragmentBinding.totalCartPrice = sum;
        return ;
    }
    override fun changeCartItemQuantity(item : CartItem, qauntity: Int, pos: Int) {
        if(item.quantity == 1 && qauntity <= 0) {
            return;
        }
       this.cartViewModel.changeCartItemQuantity(item.id , qauntity).observe(viewLifecycleOwner){
           if (it!=null && it != -1) {
            this.cartFragmentBinding.cartItemsAdapter?.cartItems!![pos].quantity = it;
               this.cartFragmentBinding.cartItemsAdapter?.notifyItemChanged(pos);
               this.setTotalPrice(this.cartFragmentBinding.cartItemsAdapter?.cartItems!!);
               cartViewModel.cartCount.value = cartViewModel.cartCount.value?.plus(qauntity);
           }
       }
    }

    override fun changeFoodInCartSize(cartItemId : Int, newSizeId: Int, position: Int) {
     this.cartViewModel.changeFoodSize ( cartItemId ,newSizeId ).observe(viewLifecycleOwner) { it ->
         if(it!= null ) {
             if(it.id == -1) {
                 this.cartFragmentBinding.isLoading = true;
                  this.cartViewModel.updateCartItems(3).observe(viewLifecycleOwner) {
                      if(it.size != cartFragmentBinding.cartItemsAdapter?.cartItems!!.size) {
                          this.cartFragmentBinding.cartItemsAdapter?.cartItems = it;
                          this.cartFragmentBinding.cartItemsAdapter?.notifyDataSetChanged();
                          this.cartFragmentBinding.isLoading = false;

                      }
                  };
                  this.cartFragmentBinding.cartItemsAdapter?.notifyDataSetChanged();
             }else {
                 this.cartFragmentBinding.cartItemsAdapter?.cartItems!![position].foodSize = it;
                 this.cartFragmentBinding.cartItemsAdapter?.notifyItemChanged(position);
             }
             this.setTotalPrice(cartFragmentBinding.cartItemsAdapter?.cartItems!!);
         }
         }
    }


}