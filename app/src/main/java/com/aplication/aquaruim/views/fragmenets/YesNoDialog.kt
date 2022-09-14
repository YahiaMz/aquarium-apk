package com.aplication.aquaruim.views.fragmenets

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.aplication.aquaruim.databinding.CustomYesNoDialogBinding

class YesNoDialog (val orderId : Int , val pos : Int) : DialogFragment() {

    lateinit var yesNoDialog : CustomYesNoDialogBinding;

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        this.yesNoDialog = CustomYesNoDialogBinding.inflate(layoutInflater);
        val alertDialog = AlertDialog.Builder(requireContext()).setView(yesNoDialog.root).create();
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));


        this.yesNoDialog.yesBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("order_Id" , orderId)
            bundle.putInt("pos" , pos)

            setFragmentResult("YES_RECEIVED" , bundle);
            this.dismiss();
        }
        this.yesNoDialog.noBtn.setOnClickListener {
            setFragmentResult("NO_NOT_RECEIVED" , Bundle());
            this.dismiss();
        }


        return  alertDialog;
    }


}