package com.viktorger.zulip_client.app.core.ui.model.mapping

import com.viktorger.zulip_client.app.core.model.MessageReceivedModel
import com.viktorger.zulip_client.app.presentation.chat.adapter.received_message.MessageReceivedDelegateItem

internal fun MessageReceivedModel.toDelegateItem(): MessageReceivedDelegateItem =
    MessageReceivedDelegateItem(this)