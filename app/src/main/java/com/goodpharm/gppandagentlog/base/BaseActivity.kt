package com.goodpharm.gppandagentlog.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<B : ViewDataBinding>(
    private val layoutRes: Int
) : AppCompatActivity() {
    protected lateinit var mBinding: B
    private lateinit var progressDialog: AppCompatDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, layoutRes)
        mBinding.lifecycleOwner = this
        observeData()
        setEvent()
    }
    abstract fun observeData()
    abstract fun setEvent()

    protected fun binding(action: B.() -> Unit) {
        mBinding.run(action)
    }

}