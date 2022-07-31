package com.aplication.aquaruim.views.fragmenets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.aplication.aquaruim.R
import com.aplication.aquaruim.databinding.FragmentFavoritesBinding

class Favorites : Fragment() {

    lateinit var favoritesBinding : FragmentFavoritesBinding ;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.favoritesBinding = DataBindingUtil.inflate(inflater , R.layout.fragment_favorites , container , false);
        return  this.favoritesBinding.root

    }


}