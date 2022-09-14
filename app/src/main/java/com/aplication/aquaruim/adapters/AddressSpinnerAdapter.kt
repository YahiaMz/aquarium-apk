package com.aplication.aquaruim.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class AddressSpinnerAdapter (val addresses : ArrayList<String> ) : BaseAdapter(){
     var layoutInflater : LayoutInflater ?=null;
    override fun getCount(): Int {
        return  addresses.size;
    }

    override fun getItem(p0: Int): Any {
      return  0;
    }

    override fun getItemId(p0: Int): Long {
        return 0;
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        if(layoutInflater == null) {
            layoutInflater = LayoutInflater.from(p2?.context);
        }

        return p1!!

    }
}