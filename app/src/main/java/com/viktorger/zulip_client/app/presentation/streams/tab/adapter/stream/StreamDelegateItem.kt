package com.viktorger.zulip_client.app.presentation.streams.tab.adapter.stream

import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem
import com.viktorger.zulip_client.app.core.ui.model.StreamModelUi

class StreamDelegateItem(
    private val value: StreamModelUi
) : DelegateItem {
    override fun content(): StreamModelUi = value

    override fun id(): Any = value.name

    override fun compareToOther(other: DelegateItem): Boolean =
        (other as StreamDelegateItem).value == value

    override fun payload(other: Any): DelegateItem.Payloadable {
        if (other is StreamDelegateItem) {
            if (value.isExpanded != other.value.isExpanded) {
                return ChangePayload.ExpandStateChanged(other.value.isExpanded)
            }
        }
        return DelegateItem.Payloadable.None
    }

    sealed class ChangePayload : DelegateItem.Payloadable {
        data class ExpandStateChanged(val isExpanded: Boolean) : ChangePayload()
    }
}