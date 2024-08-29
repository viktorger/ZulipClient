package com.viktorger.zulip_client.app.core.ui.adapters

import androidx.recyclerview.widget.DiffUtil

class DelegateAdapterItemCallback : DiffUtil.ItemCallback<DelegateItem>() {
    override fun areItemsTheSame(oldItem: DelegateItem, newItem: DelegateItem): Boolean =
        oldItem::class == newItem::class && oldItem.id() == newItem.id()

    override fun areContentsTheSame(oldItem: DelegateItem, newItem: DelegateItem): Boolean =
        oldItem.compareToOther(newItem)

    override fun getChangePayload(oldItem: DelegateItem, newItem: DelegateItem): Any? {
        return oldItem.payload(newItem)
    }
}