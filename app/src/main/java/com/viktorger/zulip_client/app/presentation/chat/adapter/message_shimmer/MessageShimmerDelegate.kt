package com.viktorger.zulip_client.app.presentation.chat.adapter.message_shimmer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viktorger.zulip_client.app.core.ui.adapters.AdapterDelegate
import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem
import com.viktorger.zulip_client.app.databinding.ItemMessageShimmerBinding

class MessageShimmerDelegate : AdapterDelegate() {
    override fun onCreateHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        ViewHolder(
            ItemMessageShimmerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DelegateItem) = Unit

    override fun isOfViewType(item: DelegateItem): Boolean = item is MessageShimmerDelegateItem

    class ViewHolder(binding: ItemMessageShimmerBinding) : RecyclerView.ViewHolder(binding.root)
}