package com.viktorger.zulip_client.app.presentation.profile

import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.core.ui.model.mapping.toUi
import com.viktorger.zulip_client.app.presentation.mvi.MviReducer

class ProfileReducer : MviReducer<
        ProfileState,
        ProfilePartialState> {
    override fun reduce(prevState: ProfileState, partialState: ProfilePartialState): ProfileState =
        when (partialState) {
            is ProfilePartialState.ProfileLoaded -> {
                prevState.copy(
                    profileUi = LceState.Content(partialState.profileModel.toUi())
                )
            }

            is ProfilePartialState.Error -> {
                if (prevState.profileUi is LceState.Content) {
                    prevState
                } else {
                    prevState.copy(
                        profileUi = LceState.Error(partialState.error)
                    )
                }
            }

            ProfilePartialState.ProfileLoading -> {
                prevState.copy(
                    profileUi = LceState.Loading
                )
            }
        }
}
