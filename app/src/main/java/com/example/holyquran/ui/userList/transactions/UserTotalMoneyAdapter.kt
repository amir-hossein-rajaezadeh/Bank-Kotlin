package com.example.holyquran.ui.userList.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.holyquran.data.model.Transaction
import com.example.holyquran.databinding.UserTotalMoneyBinding

class UserTotalMoneyAdapter() : ListAdapter<Transaction, RecyclerView.ViewHolder>(BillDiffCallback()) {
    private val ITEM_VIEW_TYPE_EMPTY = 0
    private val ITEM_VIEW_TYPE_ITEM = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            ITEM_VIEW_TYPE_EMPTY -> EmptyViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = getItem(position)
                holder.bind(item, clickListener)
            }
            is EmptyViewHolder -> {
                holder.bind()
            }
        }
    }

    lateinit var clickListener: AdapterListener
    fun setOnclickListener(listener: AdapterListener) {
        clickListener = listener
    }


    override fun getItemViewType(position: Int): Int {
        return if (itemCount > 0)
            ITEM_VIEW_TYPE_ITEM
        else
            ITEM_VIEW_TYPE_EMPTY
    }

    class ViewHolder private constructor(val binding: UserTotalMoneyBinding ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Transaction, adapterListener: AdapterListener) {
            binding.transaction = item
            binding.transaction = item
            binding.clickListener = adapterListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = UserTotalMoneyBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class EmptyViewHolder private constructor(val binding: UserTotalMoneyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): EmptyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = UserTotalMoneyBinding.inflate(layoutInflater, parent, false)
                return EmptyViewHolder(binding)
            }
        }
    }
}

class BillDiffCallback : DiffUtil.ItemCallback<Transaction>() {
    override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem.userId == newItem.userId
    }

    override fun areContentsTheSame(
        oldItem: Transaction,
        newItem: Transaction
    ): Boolean {
        return oldItem == newItem
    }
}

class AdapterListener(
    val clickListener: (id: Long) -> Unit,
    val deleteListener: (category: Transaction) -> Unit

) {
    fun onclick(category: Transaction) = clickListener(category.userId)
    fun onDeleteClick(userInfo: Transaction) = deleteListener(userInfo)

}