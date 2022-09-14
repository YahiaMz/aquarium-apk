package com.aplication.aquaruim.views.fragmenets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.aplication.aquaruim.R
import com.aplication.aquaruim.databinding.FragmentOrderPlacedWithSucccessBinding
import com.aplication.aquaruim.viewmodels.CartViewModel


class OrderPlacedWithSucccessFragment : Fragment() {

    lateinit var orderPlacedWithSuccessBinding: FragmentOrderPlacedWithSucccessBinding
    val cartViewModel : CartViewModel by activityViewModels( )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        this.orderPlacedWithSuccessBinding = FragmentOrderPlacedWithSucccessBinding.inflate(layoutInflater);
        this.orderPlacedWithSuccessBinding.backToHomeFromSuccess.setOnClickListener {
            cartViewModel.mFragmentIndex.value = 0
        }
        return this.orderPlacedWithSuccessBinding.root
    }


}