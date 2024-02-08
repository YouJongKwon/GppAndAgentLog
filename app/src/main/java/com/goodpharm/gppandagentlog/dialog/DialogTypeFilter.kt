package com.goodpharm.gppandagentlog.dialog

import androidx.lifecycle.MutableLiveData
import com.goodpharm.gppandagentlog.R
import com.goodpharm.gppandagentlog.base.BaseBottomSheetDialog
import com.goodpharm.gppandagentlog.data.Constants
import com.goodpharm.gppandagentlog.databinding.DialogTypeFilterBinding

class DialogTypeFilter(val callback: TypeCallback) :
    BaseBottomSheetDialog<DialogTypeFilterBinding>(R.layout.dialog_type_filter) {

    private var isAll = MutableLiveData(true)
    private var isGpp = MutableLiveData(true)
    private var isKiosk = MutableLiveData(true)
    private var isApi = MutableLiveData(true)
    private var isNormal = MutableLiveData(true)

    override fun binding() {

        observing()

        mBinding.btnCustomFilter.setOnClickListener {
            callback.onCustomFilter()
            dismiss()
        }

        mBinding.btnAll.setOnClickListener {
            isAll.value = !(isAll.value ?: true)
        }

        mBinding.btnGpp.setOnClickListener {
            isGpp.value = !(isGpp.value ?: true)
        }

        mBinding.btnKiosk.setOnClickListener {
            isKiosk.value = !(isKiosk.value ?: true)
        }

        mBinding.btnApi.setOnClickListener {
            isApi.value = !(isApi.value ?: true)
        }

        mBinding.btnNormal.setOnClickListener {
            isNormal.value = !(isNormal.value ?: true)
        }

        mBinding.btnConfirm.setOnClickListener {
            if (mBinding.checkBoxAll.isChecked) {
                callback.onConfirm(arrayListOf())
                dismiss()
            } else {
                val filterList = arrayListOf<Constants.Companion.AGENT_DATA_TYPE>()
                if (mBinding.checkBoxGpp.isChecked) {
                    filterList.add(Constants.Companion.AGENT_DATA_TYPE.SEND_FOR_GPP)
                    filterList.add(Constants.Companion.AGENT_DATA_TYPE.RECEPTION_BY_GPP)
                }
                if (mBinding.checkBoxKiosk.isChecked) {
                    filterList.add(Constants.Companion.AGENT_DATA_TYPE.KIOSK_SEND)
                    filterList.add(Constants.Companion.AGENT_DATA_TYPE.KIOSK_RECEIVE)
                }
                if (mBinding.checkBoxApi.isChecked) {
                    filterList.add(Constants.Companion.AGENT_DATA_TYPE.API)
                    filterList.add(Constants.Companion.AGENT_DATA_TYPE.API_RECEIVE)
                }
                if (mBinding.checkBoxNormal.isChecked) {
                    filterList.add(Constants.Companion.AGENT_DATA_TYPE.NORMAL)
                }
                callback.onConfirm(filterList)
                dismiss()
            }
        }
    }

    private fun observing() {

        isAll.observe(viewLifecycleOwner) { isCheck ->
            mBinding.checkBoxAll.isChecked = isCheck

            if (isCheck) {
                isGpp.value = true
                isKiosk.value = true
                isApi.value = true
                isNormal.value = true
            }
        }
        isGpp.observe(viewLifecycleOwner) { isCheck ->
            mBinding.checkBoxGpp.isChecked = isCheck

            if (!isCheck) {
                isAll.value = false
            }
        }
        isKiosk.observe(viewLifecycleOwner) { isCheck ->
            mBinding.checkBoxKiosk.isChecked = isCheck
            if (!isCheck) {
                isAll.value = false
            }
        }
        isApi.observe(viewLifecycleOwner) { isCheck ->
            mBinding.checkBoxApi.isChecked = isCheck
            if (!isCheck) {
                isAll.value = false
            }
        }
        isNormal.observe(viewLifecycleOwner) { isCheck ->
            mBinding.checkBoxNormal.isChecked = isCheck
            if (!isCheck) {
                isAll.value = false
            }
        }

    }

    interface TypeCallback {
        fun onConfirm(filterList: ArrayList<Constants.Companion.AGENT_DATA_TYPE>)
        fun onCustomFilter()
    }
}