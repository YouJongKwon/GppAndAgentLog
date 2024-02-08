package com.goodpharm.gppandagentlog.dialog

import android.view.inputmethod.EditorInfo
import androidx.lifecycle.MutableLiveData
import com.goodpharm.gppandagentlog.R
import com.goodpharm.gppandagentlog.base.BaseBottomSheetDialog
import com.goodpharm.gppandagentlog.data.Constants
import com.goodpharm.gppandagentlog.databinding.DialogTypeFilterBinding
import com.goodpharm.gppandagentlog.databinding.DialogWordSearchBinding

class DialogSearch(val callback: (String) -> Unit) : BaseBottomSheetDialog<DialogWordSearchBinding>(R.layout.dialog_word_search){

    override fun binding() {
        observing()

        mBinding.btnConfirm.setOnClickListener {
            val word = mBinding.etSearch.text.toString()
            if(word.isEmpty()){
                dismiss()
            }else {
                callback(word)
                dismiss()
            }
        }

        mBinding.etSearch.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                mBinding.btnConfirm.performClick()
                true
            }else {
                false
            }

        }
    }

    private fun observing(){

    }

}