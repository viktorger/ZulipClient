package com.viktorger.zulip_client.app.presentation.streams.tab.adapter.topic

import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem
import com.viktorger.zulip_client.app.core.ui.model.TopicModelUi

class TopicDelegateItem(
    private val value: TopicModelUi
) : DelegateItem {
    override fun content(): TopicModelUi = value

    override fun id(): Any = "${value.streamName}_${value.topicName}"

    override fun compareToOther(other: DelegateItem): Boolean =
        (other as TopicDelegateItem).value == value

    override fun payload(other: Any): DelegateItem.Payloadable {
        if (other is TopicDelegateItem) {
            if (value.messagesCount != other.value.messagesCount) {
                return ChangePayload.MessageCountChange(other.value.messagesCount)
            }
        }
        return DelegateItem.Payloadable.None
    }

    sealed class ChangePayload : DelegateItem.Payloadable {
        data class MessageCountChange(val messageCount: Int) : ChangePayload()
    }
}