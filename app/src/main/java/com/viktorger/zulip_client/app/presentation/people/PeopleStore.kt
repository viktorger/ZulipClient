package com.viktorger.zulip_client.app.presentation.people

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.presentation.mvi.MviStore
import javax.inject.Inject

class PeopleStore(
    actor: PeopleActor,
    reducer: PeopleReducer
) : MviStore<PeoplePartialState, PeopleIntent, PeopleState, PeopleEffect>(
    actor, reducer
) {
    override fun initialSateCreator(): PeopleState = PeopleState(LceState.Loading)

    class Factory @Inject constructor(
        private val actor: PeopleActor
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PeopleStore(actor, PeopleReducer()) as T
        }
    }
}
