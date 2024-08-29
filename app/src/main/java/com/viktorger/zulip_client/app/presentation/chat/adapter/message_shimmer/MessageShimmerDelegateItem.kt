package com.viktorger.zulip_client.app.presentation.chat.adapter.message_shimmer

import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem
import com.viktorger.zulip_client.app.core.ui.model.MessagePageTag

data class MessageShimmerDelegateItem(
    private val messagePageTag: MessagePageTag
) : DelegateItem {
    override fun content(): MessagePageTag = messagePageTag

    override fun id(): MessagePageTag = messagePageTag

    override fun compareToOther(other: DelegateItem): Boolean =
        (other as MessageShimmerDelegateItem).messagePageTag == messagePageTag
}