package com.viktorger.zulip_client.app.presentation.foreign_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.presentation.mvi.MviStore
import javax.inject.Inject

class ForeignProfileStore(
    actor: ForeignProfileActor,
    reducer: ForeignProfileReducer
) : MviStore<
        ForeignProfilePartialState,
        ForeignProfileIntent,
        ForeignProfileState,
        ForeignProfileEffect>(actor, reducer) {
    override fun initialSateCreator(): ForeignProfileState = ForeignProfileState(LceState.Loading)

    class Factory @Inject constructor(
        private val actor: ForeignProfileActor
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ForeignProfileStore(actor, ForeignProfileReducer()) as T
        }
    }
}