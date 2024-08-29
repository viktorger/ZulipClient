package com.viktorger.zulip_client.app.presentation.streams.tab

import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem
import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.presentation.mvi.MviState

data class StreamTabState(
    val streamsUi: LceState<List<DelegateItem>>
) : MviState