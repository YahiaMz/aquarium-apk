package com.aplication.aquaruim.utils

import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.aplication.aquaruim.R
import com.squareup.picasso.Picasso

@BindingAdapter("bind:customHeight")
fun putLayoutHeight(view: View, height: Float) {
    view.layoutParams = view.layoutParams.apply { this.height = height.toInt() }
}

@BindingAdapter("bind:customSize")
fun setLayoutHeight(view: View, size: Float) {
    val layoutParams: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(size.toInt() , size.toInt())
    layoutParams.topToTop;
    view.setLayoutParams(layoutParams)
}

@BindingAdapter("bind:layout_widthCircle2")
fun setLayoutSizeCircle2(view: View, size: Float) {
    val layoutParams: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(size.toInt() , size.toInt())

    val parent = R.id.orderStatusCl;

    layoutParams.topToTop = parent;
    layoutParams.bottomToBottom = parent;
    layoutParams.leftToRight = R.id.statusLine1;
    layoutParams.rightToRight = R.id.statusLine2

    view.setLayoutParams(layoutParams)
}

@BindingAdapter("bind:layout_widthCircle3")
fun setLayoutSizeCircle3(view: View, size: Float) {
    val layoutParams: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(size.toInt() , size.toInt())

    val parent = R.id.orderStatusCl;

    layoutParams.topToTop = parent;
    layoutParams.bottomToBottom = parent;
    layoutParams.leftToRight = R.id.statusLine2;
    layoutParams.rightToRight = R.id.statusLine3
    view.setLayoutParams(layoutParams)
}

@BindingAdapter("bind:layout_widthCircle4")
fun setLayoutSizeCircle4(view: View, size: Float) {
    val layoutParams: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(size.toInt() , size.toInt())

    val parent = R.id.orderStatusCl;

    layoutParams.topToTop = parent;
    layoutParams.bottomToBottom = parent;
    layoutParams.rightToLeft = R.id.statusLine4
    layoutParams.leftToRight = R.id.statusLine3;

    view.setLayoutParams(layoutParams)
}

@BindingAdapter("bind:layout_widthCircle5")
fun setLayoutSizeCircle5(view: View, size: Float) {
    val layoutParams: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(size.toInt() , size.toInt())

    val parent = R.id.orderStatusCl;

    layoutParams.topToTop = parent;
    layoutParams.bottomToBottom = parent;
    layoutParams.rightToRight = parent
    layoutParams.leftToRight = R.id.statusLine4;

    view.setLayoutParams(layoutParams)
}



@BindingAdapter("bind:foodImage")
fun setFoodImage(foodImageView: ImageView , imageUrl : String) {
    Picasso.get().load(API_URLS.FOOD_IMAGES_URL + imageUrl ).into(foodImageView);
}
