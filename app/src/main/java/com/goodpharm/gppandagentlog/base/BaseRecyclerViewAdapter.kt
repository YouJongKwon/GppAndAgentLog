package com.goodpharm.gppandagentlog.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

open class BaseRecyclerViewAdapter<T, VB : ViewBinding>(
    @LayoutRes private val layoutId: Int,
    private val onBind: (adapter: BaseRecyclerViewAdapter<T, VB>, item: T, binding: VB) -> Unit
) : RecyclerView.Adapter<BaseRecyclerViewAdapter.ItemViewHolder<T, VB>>() {

    val list = arrayListOf<T>()
    private lateinit var mBinding: VB

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<T, VB> {
        val layoutInflater = LayoutInflater.from(parent.context)
        mBinding = DataBindingUtil.inflate(layoutInflater, layoutId, parent, false)
        return ItemViewHolder(this, mBinding, onBind)
    }

    override fun onBindViewHolder(holder: ItemViewHolder<T, VB>, position: Int) {
        holder.onBinding(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun setList(list: ArrayList<T>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun addList(list: ArrayList<T>) {
        val index = this.list.size
        this.list.addAll(list)
        notifyItemRangeInserted(index, this.list.size)
    }

    fun addItem(item: T) {
        list.add(item)
        notifyItemInserted(list.size - 1)
    }

    fun addItem(index: Int, item: T) {
        list.add(index, item)
        notifyItemInserted(index)
    }

    fun removeItem(index: Int) {
        list.removeAt(index)
        notifyItemRemoved(index)
    }


    fun updateItem(item: T) {
        notifyItemChanged(list.indexOf(item))
    }

    class ItemViewHolder<T, VB : ViewBinding>(
        val adapter: BaseRecyclerViewAdapter<T, VB>,
        val mBinding: VB,
        val onBind: (adapter: BaseRecyclerViewAdapter<T, VB>, item: T, binding: VB) -> Unit
    ) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun onBinding(item: T) {
            onBind(adapter, item, mBinding)
        }
    }

}