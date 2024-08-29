package com.viktorger.zulip_client.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.presentation.mvi.MviStore
import javax.inject.Inject

class ProfileStore @Inject constructor(
    actor: ProfileActor,
    reducer: ProfileReducer
) : MviStore<
        ProfilePartialState,
        ProfileIntent,
        ProfileState,
        ProfileEffect>(actor, reducer) {

    init {
        postIntent(ProfileIntent.Init)
    }
    override fun initialSateCreator(): ProfileState = ProfileState(LceState.Loading)

    class Factory @Inject constructor(
        private val actor: ProfileActor
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileStore(actor, ProfileReducer()) as T
        }
    }
}