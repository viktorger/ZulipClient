package com.viktorger.zulip_client.app.core.ui.model

import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem

data class MessagePageLoadAnswerUi (
    val chat: List<DelegateItem>,
    val limitReached: Boolean
)