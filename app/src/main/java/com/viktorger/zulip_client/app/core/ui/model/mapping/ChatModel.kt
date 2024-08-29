package com.viktorger.zulip_client.app.core.ui.model.mapping

import com.viktorger.zulip_client.app.core.common.areNotDateEqual
import com.viktorger.zulip_client.app.core.common.dateDelegateItem
import com.viktorger.zulip_client.app.core.model.ChatModel
import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem
import com.viktorger.zulip_client.app.presentation.chat.adapter.received_message.MessageReceivedDelegateItem
import com.viktorger.zulip_client.app.presentation.chat.adapter.sent_message.MessageSentDelegateItem

internal fun ChatModel.toDelegateItems(): List<DelegateItem> {
    val receivedMessages = receivedMessages.reversed()
    val sentMessages = sentMessages.reversed()

    if (receivedMessages.isEmpty() && sentMessages.isEmpty()) {
        return emptyList()
    }
    val delegateItemList: MutableList<DelegateItem> = mutableListOf()

    var receivedPointer = 0
    var sentPointer = 0
    val firstDateTimestamp = when {
        receivedMessages.isNotEmpty() && sentMessages.isNotEmpty() -> {
            maxOf(sentMessages.first().timestamp, receivedMessages.first().timestamp)
        }

        receivedMessages.isEmpty() -> {
            sentMessages.first().timestamp
        }

        else -> {
            receivedMessages.first().timestamp
        }
    }
    var currentDayTimestamp: Long = firstDateTimestamp

    // Merging sorted lists
    while (receivedPointer < receivedMessages.size && sentPointer < sentMessages.size) {
        if (sentMessages[sentPointer].timestamp > receivedMessages[receivedPointer].timestamp) {
            val sentMessageToAdd = sentMessages[sentPointer]
            val sentMessageToAddTimestamp = sentMessageToAdd.timestamp
            if (areNotDateEqual(sentMessageToAddTimestamp, currentDayTimestamp)) {
                delegateItemList.add(
                    dateDelegateItem(currentDayTimestamp)
                )
                currentDayTimestamp = sentMessageToAddTimestamp
            }
            delegateItemList.add(sentMessageToAdd.toDelegateItem())
            sentPointer++
        } else {
            val receivedMessageToAdd = receivedMessages[receivedPointer]
            val receivedMessageToAddDate = receivedMessageToAdd.timestamp
            if (areNotDateEqual(receivedMessageToAddDate, currentDayTimestamp)) {
                delegateItemList.add(
                    dateDelegateItem(currentDayTimestamp)
                )
                currentDayTimestamp = receivedMessageToAddDate
            }
            delegateItemList.add(receivedMessageToAdd.toDelegateItem())
            receivedPointer++
        }
    }

    for (i in sentPointer until sentMessages.size) {
        val sentMessageToAdd = sentMessages[i]
        val sentMessageToAddDate = sentMessageToAdd.timestamp
        if (areNotDateEqual(sentMessageToAddDate, currentDayTimestamp)) {
            delegateItemList.add(
                dateDelegateItem(currentDayTimestamp)
            )
            currentDayTimestamp = sentMessageToAddDate
        }
        delegateItemList.add(MessageSentDelegateItem(sentMessageToAdd))
    }

    for (i in receivedPointer until receivedMessages.size) {
        val receivedMessageToAdd = receivedMessages[i]
        val receivedMessageToAddDate = receivedMessageToAdd.timestamp
        if (areNotDateEqual(receivedMessageToAddDate, currentDayTimestamp)) {
            delegateItemList.add(
                dateDelegateItem(currentDayTimestamp)
            )
            currentDayTimestamp = receivedMessageToAddDate
        }
        delegateItemList.add(MessageReceivedDelegateItem(receivedMessageToAdd))
    }

    delegateItemList.add(
        dateDelegateItem(currentDayTimestamp)
    )
    return delegateItemList.reversed()
}