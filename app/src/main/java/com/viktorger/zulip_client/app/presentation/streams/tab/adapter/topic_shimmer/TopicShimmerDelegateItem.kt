package com.viktorger.zulip_client.app.presentation.streams.tab.adapter.topic_shimmer

import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem

class TopicShimmerDelegateItem(
    val streamName: String
) : DelegateItem {
    override fun content(): String = streamName

    override fun id(): Any = streamName

    override fun compareToOther(other: DelegateItem): Boolean =
        (other as TopicShimmerDelegateItem).streamName == streamName
}