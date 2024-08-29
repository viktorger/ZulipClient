package com.viktorger.zulip_client.app.core.ui.model.mapping

import com.viktorger.zulip_client.app.core.model.MessageSentModel
import com.viktorger.zulip_client.app.presentation.chat.adapter.sent_message.MessageSentDelegateItem

internal fun MessageSentModel.toDelegateItem(): MessageSentDelegateItem = MessageSentDelegateItem(
    this
)