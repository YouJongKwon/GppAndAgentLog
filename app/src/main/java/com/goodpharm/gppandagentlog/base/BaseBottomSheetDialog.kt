package com.goodpharm.gppandagentlog.base

import android.app.Dialog
import android.app.Service
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.goodpharm.gppandagentlog.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * 바텀다이얼로그 통합
 */
abstract class BaseBottomSheetDialog<VB : ViewBinding>(@LayoutRes private val layoutId: Int): BottomSheetDialogFragment(){
    lateinit var mBinding: VB

    var canBackPressFlag: Boolean = false
    var onBackPressedCallback: OnBackPressedCallback? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)
        canBackPressFlag = false
        return super.onCreateDialog(savedInstanceState)
    }
    override fun onResume() {
        super.onResume()
        canBackPressFlag = true
        isCancelable = true
        binding {
            onBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if(canBackPressFlag) {
                        dismiss()
                    } else {
                        true
                    }
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                onBackPressedCallback!!
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        canBackPressFlag = false
        onBackPressedCallback?.remove()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val layoutInflater = LayoutInflater.from(context)
        mBinding = DataBindingUtil.inflate(layoutInflater, layoutId, container, false)
        isCancelable = false
        canBackPressFlag = false
        init()
        binding()

        return mBinding.root
    }

    abstract fun binding()

    private fun init(){

        //isCancelable = false

        val bottomSheetDialog = dialog as BottomSheetDialog
        val behavior = bottomSheetDialog.behavior
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

    }

    fun binding(action: VB.() -> Unit) {
        mBinding.run(action)
    }


}