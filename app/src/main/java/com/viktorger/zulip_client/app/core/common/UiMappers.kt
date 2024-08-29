package com.viktorger.zulip_client.app.core.common

import com.viktorger.zulip_client.app.core.model.StreamModel
import com.viktorger.zulip_client.app.core.model.TopicModel
import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem
import com.viktorger.zulip_client.app.core.ui.model.TopicModelUi
import com.viktorger.zulip_client.app.core.ui.model.mapping.toUi
import com.viktorger.zulip_client.app.presentation.chat.adapter.date.DateDelegateItem
import com.viktorger.zulip_client.app.presentation.chat.adapter.date.DateModelUi
import com.viktorger.zulip_client.app.presentation.streams.tab.adapter.stream.StreamDelegateItem
import com.viktorger.zulip_client.app.presentation.streams.tab.adapter.topic.TopicDelegateItem
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun areNotDateEqual(
    sentMessageToAddTimestamp: Long,
    currentDayFirstTimestamp: Long
) = sentMessageToAddTimestamp.toFullDateString() != currentDayFirstTimestamp.toFullDateString()

private fun Long.toYearString(): String {
    val formatter = SimpleDateFormat("yyyy", Locale.getDefault())
    return formatter.format(this)
}

internal fun dateDelegateItem(dateTimestamp: Long): DelegateItem =
    DateDelegateItem(
        DateModelUi(
            year = dateTimestamp.toYearString(),
            date = dateTimestamp.toDayMonthString()
        )
    )

private fun Long.toDayMonthString(): String {
    val formatter = SimpleDateFormat("d MMM", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }
    return formatter.format(this)
}

fun Long.toFullDateString(): String {
    val formatter = SimpleDateFormat("yyyy d MMM", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }
    return formatter.format(this)
}

internal fun streamModelsToDelegateItems(
    streamModelUIs: List<StreamModel>
): List<DelegateItem> {
    if (streamModelUIs.isEmpty())
        return listOf()

    val delegateItemList: MutableList<DelegateItem> = mutableListOf()
    streamModelUIs.forEach { streamModel ->
        delegateItemList.add(
            StreamDelegateItem(streamModel.toUi())
        )
    }

    return delegateItemList
}

internal fun topicModelsToDelegateItems(topicModels: List<TopicModel>): List<DelegateItem> {
    if (topicModels.isEmpty())
        return listOf()

    val delegateItemList: MutableList<DelegateItem> = mutableListOf()
    topicModels.forEach { topicModel ->
        delegateItemList.add(
            topicDelegateItem(topicModel)
        )
    }

    return delegateItemList
}

private fun topicDelegateItem(topicModel: TopicModel) =
    TopicDelegateItem(
        TopicModelUi(
            topicName = topicModel.topicName,
            streamName = topicModel.streamName,
            messagesCount = topicModel.messagesUnread,
            color = getAssociatedColor(topicModel.topicName)
        )
    )