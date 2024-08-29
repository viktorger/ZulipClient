package com.viktorger.zulip_client.app.core.ui.adapters

interface DelegateItem {
    fun content(): Any
    fun id(): Any
    fun compareToOther(other: DelegateItem) : Boolean
    fun payload(other: Any): Payloadable = Payloadable.None
    interface Payloadable {
        object None : Payloadable
    }
}