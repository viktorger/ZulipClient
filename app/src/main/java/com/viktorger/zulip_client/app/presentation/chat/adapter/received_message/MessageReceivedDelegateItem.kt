package com.viktorger.zulip_client.app.presentation.chat.adapter.received_message

import com.viktorger.zulip_client.app.core.model.MessageReceivedModel
import com.viktorger.zulip_client.app.core.model.ReactionWithCounter
import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem

data class MessageReceivedDelegateItem(
    private val value: MessageReceivedModel
) : DelegateItem {
    override fun content(): MessageReceivedModel = value

    override fun id(): Any = value.messageId

    override fun compareToOther(other: DelegateItem): Boolean =
        (other as MessageReceivedDelegateItem).value == value

    override fun payload(other: Any): DelegateItem.Payloadable {
        if (other is MessageReceivedDelegateItem) {
            if (value.reactionsWithCounter != other.value.reactionsWithCounter) {
                return ChangePayload.ReactionsChanged(other.value.reactionsWithCounter)
            }
        }
        return DelegateItem.Payloadable.None
    }

    sealed class ChangePayload : DelegateItem.Payloadable {
        data class ReactionsChanged(val reactions: List<ReactionWithCounter>) : ChangePayload()
    }
}