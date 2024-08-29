package com.viktorger.zulip_client.app.presentation.chat.adapter.date

import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem

data class DateDelegateItem(
    private val value: DateModelUi
) : DelegateItem {
    override fun content(): DateModelUi = value

    override fun id(): String = value.getFullDate()

    override fun compareToOther(other: DelegateItem): Boolean =
        (other as DateDelegateItem).value == value
}