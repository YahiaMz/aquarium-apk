package com.aplication.aquaruim.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aplication.aquaruim.R
import com.aplication.aquaruim.databinding.CategoryItemContainerBinding
import com.aplication.aquaruim.interfaces.IonCategorySelected
import com.aplication.aquaruim.models.Category
import com.aplication.aquaruim.utils.API_URLS
import com.squareup.picasso.Picasso

class CategoriesAdapter public constructor(val ionCategorySelected: IonCategorySelected , val categories : ArrayList<Category>): RecyclerView.Adapter<CategoriesAdapter.ItemContainerHolder> () {

    lateinit var categoryItemContainerBinding: CategoryItemContainerBinding;
    lateinit var mLayoutInflater: LayoutInflater;
    var selectedCategoryIndex = 0;



    public class ItemContainerHolder: RecyclerView.ViewHolder {

       var itemBinding: CategoryItemContainerBinding;
        constructor(itemBinding: CategoryItemContainerBinding) : super(itemBinding.root) {
            this.itemBinding = itemBinding;
        }

        public fun bind(category: Category , isSelected : Boolean) {
            Picasso.get().load(API_URLS.CATEGORIES_IMAGES_URL+ category.imageUrl ).into(this.itemBinding.categoryItemImage);
            this.itemBinding.currentCategory = category;
          this.itemBinding.imageCl.setBackgroundResource(if (isSelected == true) R.drawable.cateogry_item_bg_selected else R.drawable.cateogry_item_bg)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemContainerHolder {
            this.mLayoutInflater = LayoutInflater.from(parent.context);

        this.categoryItemContainerBinding =
            CategoryItemContainerBinding.inflate(mLayoutInflater, parent, false);
        return ItemContainerHolder(this.categoryItemContainerBinding);
    }
    override fun onBindViewHolder(holder: ItemContainerHolder, position: Int) {
        var category : Category = categories[position];
        holder.bind(category, position == selectedCategoryIndex);
        holder.itemBinding.categoryItemCL.setOnClickListener {
            notifyItemChanged(selectedCategoryIndex);
            selectedCategoryIndex = position;
            notifyItemChanged(position);
            this.ionCategorySelected.onCategorySelected(category)
        }
    }
    override fun getItemCount(): Int {
        return categories.size
    }


}