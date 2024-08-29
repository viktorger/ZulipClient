package com.viktorger.zulip_client.app.presentation.people

import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.core.ui.model.UserShortcutModelUi
import com.viktorger.zulip_client.app.core.ui.model.UserStatusUi
import com.viktorger.zulip_client.app.presentation.mvi.MviState

data class PeopleState(
    val peopleUi: LceState<List<UserShortcutModelUi>>,
    val statusMap: Map<Int, UserStatusUi> = mapOf()
) : MviState