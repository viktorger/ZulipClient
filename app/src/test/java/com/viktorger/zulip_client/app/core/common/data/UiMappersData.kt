package com.viktorger.zulip_client.app.core.common.data

import com.viktorger.zulip_client.app.core.model.ChatModel
import com.viktorger.zulip_client.app.core.model.MessageReceivedModel
import com.viktorger.zulip_client.app.core.model.MessageSentModel
import com.viktorger.zulip_client.app.presentation.chat.adapter.date.DateDelegateItem
import com.viktorger.zulip_client.app.presentation.chat.adapter.date.DateModelUi
import com.viktorger.zulip_client.app.presentation.chat.adapter.received_message.MessageReceivedDelegateItem
import com.viktorger.zulip_client.app.presentation.chat.adapter.sent_message.MessageSentDelegateItem

class UiMappersData {
    val chatModel = ChatModel(
        receivedMessages = listOf(
            MessageReceivedModel(
                messageId = 0,
                userId = 4829,
                username = "Gabriela Trujillo",
                timestamp = 10000000000,
                content = "tation",
                avatarUrl = null,
                reactionsWithCounter = listOf()
            ),
            MessageReceivedModel(
                messageId = 2,
                userId = 8755,
                username = "Arnulfo Velazquez",
                timestamp = 10152000000,
                content = "regione",
                avatarUrl = null,
                reactionsWithCounter = emptyList()
            )
        ),
        sentMessages = listOf(
            MessageSentModel(
                messageId = 1,
                timestamp = 10065600000,
                content = "audire",
                reactionsWithCounter = listOf()
            ),
            MessageSentModel(
                messageId = 3,
                timestamp = 10238400000,
                content = "ea",
                reactionsWithCounter = emptyList()
            )
        )
    )

    val delegateList = listOf(
        dateDelegateItem1,
        MessageReceivedDelegateItem(chatModel.receivedMessages[0]),
        dateDelegateItem2,
        MessageSentDelegateItem(chatModel.sentMessages[0]),
        dateDelegateItem3,
        MessageReceivedDelegateItem(chatModel.receivedMessages[1]),
        dateDelegateItem4,
        MessageSentDelegateItem(chatModel.sentMessages[1]),
    )

    companion object {
        val dateDelegateItem1 = DateDelegateItem(DateModelUi("1970", "26 апр."))
        val dateDelegateItem2 = DateDelegateItem(DateModelUi("1970", "27 апр."))
        val dateDelegateItem3 = DateDelegateItem(DateModelUi("1970", "28 апр."))
        val dateDelegateItem4 = DateDelegateItem(DateModelUi("1970", "29 апр."))
    }
}