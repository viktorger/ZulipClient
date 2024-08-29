package com.viktorger.zulip_client.app.core.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class AdapterDelegate {
    abstract fun onCreateHolder(parent: ViewGroup): RecyclerView.ViewHolder
    abstract fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DelegateItem)
    abstract fun isOfViewType(item: DelegateItem): Boolean

    open fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: DelegateItem,
        payloads: List<DelegateItem.Payloadable>
    ) {
        onBindViewHolder(holder, item)
    }
}