package com.viktorger.zulip_client.app.presentation.streams.tab.adapter.topic_shimmer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viktorger.zulip_client.app.core.ui.adapters.AdapterDelegate
import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem
import com.viktorger.zulip_client.app.databinding.ItemTopicShimmerBinding

class TopicShimmerDelegate : AdapterDelegate() {
    override fun onCreateHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        ViewHolder(
            ItemTopicShimmerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DelegateItem) = Unit

    override fun isOfViewType(item: DelegateItem): Boolean = item is TopicShimmerDelegateItem

    class ViewHolder(binding: ItemTopicShimmerBinding) : RecyclerView.ViewHolder(binding.root)
}