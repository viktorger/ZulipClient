package com.viktorger.zulip_client.app.core.ui.model

sealed class LceState<out T> {
    data object Loading: LceState<Nothing>()
    data class Error(val error: Throwable): LceState<Nothing>()
    data class Content<T>(val data: T): LceState<T>()
}