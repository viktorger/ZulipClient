package com.viktorger.zulip_client.app.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.presentation.mvi.MviStore
import javax.inject.Inject

class ChatStore(
    actor: ChatActor,
    reducer: ChatReducer
) : MviStore<
        ChatPartialState,
        ChatIntent,
        ChatState,
        ChatEffect>(actor, reducer) {
    override fun initialSateCreator(): ChatState = ChatState(LceState.Loading)

    class Factory @Inject constructor(
        private val actor: ChatActor
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChatStore(actor, ChatReducer()) as T
        }
    }
}