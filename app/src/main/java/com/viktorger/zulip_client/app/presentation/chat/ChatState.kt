package com.viktorger.zulip_client.app.presentation.chat

import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem
import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.presentation.mvi.MviState

data class ChatState(
    val chatUi: LceState<List<DelegateItem>>,
    val nextPageLimitReached: Boolean = false,
    val previousPageLimitReached: Boolean = false,
    val shouldScrollNextRenderToEnd: Boolean = false
) : MviState