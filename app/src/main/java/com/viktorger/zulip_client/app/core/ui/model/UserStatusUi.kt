package com.viktorger.zulip_client.app.core.ui.model

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.viktorger.zulip_client.app.R

sealed class UserStatusUi(
    @StringRes val text: Int,
    @ColorRes val color: Int
) {
    data object Active : UserStatusUi(R.string.status_active, R.color.status_active_color)
    data object Idle : UserStatusUi(R.string.status_idle, R.color.status_idle_color)
    data object Offline : UserStatusUi(R.string.status_offline, R.color.status_offline_color)
    data object Unknown : UserStatusUi(R.string.status_unknown, android.R.color.transparent)
}
