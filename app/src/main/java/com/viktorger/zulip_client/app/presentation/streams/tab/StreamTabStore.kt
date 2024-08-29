package com.viktorger.zulip_client.app.presentation.streams.tab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.presentation.mvi.MviStore
import javax.inject.Inject

class StreamTabStore(
    actor: StreamTabActor,
    reducer: StreamTabReducer
) : MviStore<
        StreamTabPartialState,
        StreamTabIntent,
        StreamTabState,
        StreamTabEffect>(actor, reducer) {
    override fun initialSateCreator(): StreamTabState = StreamTabState(LceState.Loading)

    class Factory @Inject constructor(
        private val actor: StreamTabActor
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StreamTabStore(actor, StreamTabReducer()) as T
        }
    }
}