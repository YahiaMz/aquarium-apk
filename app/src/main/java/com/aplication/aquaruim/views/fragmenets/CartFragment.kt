package com.aplication.aquaruim.views.fragmenets

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aplication.aquaruim.adapters.CartAdapter
import com.aplication.aquaruim.databinding.FragmentCartBinding
import com.aplication.aquaruim.interfaces.IChangeCartFoodSize
import com.aplication.aquaruim.interfaces.IChangeCartItemQuantity
import com.aplication.aquaruim.interfaces.IonRemoveItemFromCart
import com.aplication.aquaruim.models.CartItem
import com.aplication.aquaruim.utils.MResponseStatus
import com.aplication.aquaruim.viewmodels.CartViewModel
import com.aplication.aquaruim.views.customViews.FailToast


class CartFragment : Fragment(), IonRemoveItemFromCart, IChangeCartItemQuantity,
    IChangeCartFoodSize {

    private var isChangingItemQuantity = false
    private lateinit var cartFragmentBinding: FragmentCartBinding;
    val cartViewModel: CartViewModel by activityViewModels();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        this.cartFragmentBinding = FragmentCartBinding.inflate(inflater);
        this.cartFragmentBinding.isThereNetworkProblem = false;


        watchCartItems();
        onRefreshClicked()
        onCheckoutBtnClicked();
        return this.cartFragmentBinding.root;
    }


    private fun watchCartItems() {
        cartFragmentBinding.isLoading = true;
        this.cartFragmentBinding.isThereNetworkProblem = false
        this.cartViewModel.mutableCartItems.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it.status) {
                    MResponseStatus.SUCCESS_RESPONSE -> {
                        cartFragmentBinding.isCartEmpty = it.cartItems!!.isEmpty();
                        this.cartFragmentBinding.isThereNetworkProblem = false
                        cartFragmentBinding.cartItemsAdapter =
                            CartAdapter(it.cartItems, this, this, this);
                        setTotalPrice(it.cartItems);
                    }
                    MResponseStatus.NO_INTERNET -> {
                        cartFragmentBinding.isThereNetworkProblem = true;
                    }
                    else -> {
                        FailToast.showFailToast(context = requireContext(),
                            message = "something wrong")
                    }
                }



                cartFragmentBinding.isLoading = false;


            }

        }
    }


    private fun onRefreshClicked() {

        this.cartFragmentBinding.refreshNetwork.setOnClickListener {
            this.cartViewModel.fetchCartItems()
            this.watchCartItems();

        }
    }

    private fun onCheckoutBtnClicked() {
        this.cartFragmentBinding.checkoutBtn.setOnClickListener {
//            val toCheckOutActivity  = Intent(activity, CheckoutActivity::class.java)
//            startActivity(toCheckOutActivity)

//            activity!!.supportFragmentManager.beginTransaction()
//                .replace(R.id.mainFrameLayout, CheckoutFragment()).commit();
            this.cartViewModel.mFragmentIndex.value = 6
        }
    }

    override fun onRemoveItemCartBtnClicked(cartItem_Id: Int, quantity: Int, pos: Int) {
        this.cartViewModel.removeFoodFromUserCart(cartItem_Id).observe(viewLifecycleOwner) {
            if (it != null && it) {

                this.cartFragmentBinding.cartItemsAdapter?.cartItems?.removeAt(pos);
                this.cartFragmentBinding.cartItemsAdapter?.notifyItemRemoved(pos)
                this.cartFragmentBinding.cartItemsAdapter?.notifyItemRangeChanged(pos,
                    this.cartFragmentBinding.cartItemsAdapter?.cartItems!!.size)

                setTotalPrice(this.cartFragmentBinding.cartItemsAdapter?.cartItems!!);
                if (cartFragmentBinding.cartItemsAdapter?.cartItems!!.isEmpty()) {
                    cartFragmentBinding.isCartEmpty = true;
                }
                this.cartViewModel.cartCount.value = this.cartViewModel.cartCount.value?.minus(
                    quantity);

            }
        }
    }

    private fun setTotalPrice(arrayList: ArrayList<CartItem>) {
        var sum = 0;
        for (x in 0 until arrayList.size) {
            val cartItem: CartItem = arrayList[x];

            if (cartItem.foodSize == null)
                sum += cartItem.quantity * cartItem.food.price!!;
            else
                sum += cartItem.foodSize!!.sizePrice * cartItem.quantity;
        }
        this.cartFragmentBinding.totalCartPrice = sum;
        return;
    }

    override fun changeCartItemQuantity(item: CartItem, quantity: Int, pos: Int) {
        if (isChangingItemQuantity) {
            return;
        }
        if (item.quantity == 1 && quantity <= 0) {
            return;
        }

        isChangingItemQuantity = true

        this.cartViewModel.changeCartItemQuantity(item.id, quantity).observe(viewLifecycleOwner) {
        if (it != null) {
                if (it != -1) {


                    this.cartFragmentBinding.cartItemsAdapter?.cartItems!![pos].quantity = it;
                    this.cartFragmentBinding.cartItemsAdapter?.notifyItemChanged(pos);
                    this.setTotalPrice(this.cartFragmentBinding.cartItemsAdapter?.cartItems!!);
                    cartViewModel.cartCount.value = cartViewModel.cartCount.value?.plus(quantity);

                }
                this.isChangingItemQuantity =false
            }
        }
    }

    override fun changeFoodInCartSize(cartItemId: Int, newSizeId: Int, position: Int) {
        this.cartViewModel.changeFoodSize(cartItemId, newSizeId).observe(viewLifecycleOwner) { it ->
            if (it != null) {
                if (it.id == -1) {
                    this.cartFragmentBinding.isLoading = true;
                    this.cartViewModel.updateCartItems().observe(viewLifecycleOwner) {
                        if (it != null) {
                            if (it.status == MResponseStatus.SUCCESS_RESPONSE) {
                                this.cartFragmentBinding.cartItemsAdapter?.cartItems =
                                    it.cartItems!!;
                                this.cartFragmentBinding.cartItemsAdapter?.notifyDataSetChanged();
                                this.cartFragmentBinding.isLoading = false;
                            } else if (it.status == MResponseStatus.NO_INTERNET) {
                                FailToast.showFailToast("Internet error", requireContext());
                            }
                        }
                    };
//                    this.cartFragmentBinding.cartItemsAdapter?.notifyDataSetChanged();
                } else {
                    this.cartFragmentBinding.cartItemsAdapter?.cartItems!![position].foodSize = it;
                    this.cartFragmentBinding.cartItemsAdapter?.notifyItemChanged(position);
                }
                this.setTotalPrice(cartFragmentBinding.cartItemsAdapter?.cartItems!!);
            }
        }
    }

}

