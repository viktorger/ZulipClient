package com.viktorger.zulip_client.app.presentation.chat.adapter.sent_message

import com.viktorger.zulip_client.app.core.model.MessageSentModel
import com.viktorger.zulip_client.app.core.model.ReactionWithCounter
import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem

data class MessageSentDelegateItem(
    private val value: MessageSentModel
) : DelegateItem {
    override fun content(): MessageSentModel = value

    override fun id(): Any = value.messageId

    override fun compareToOther(other: DelegateItem): Boolean =
        (other as MessageSentDelegateItem).value == value

    override fun payload(other: Any): DelegateItem.Payloadable {
        if (other is MessageSentDelegateItem) {
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