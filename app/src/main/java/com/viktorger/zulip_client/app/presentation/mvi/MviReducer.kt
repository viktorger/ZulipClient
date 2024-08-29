package com.viktorger.zulip_client.app.presentation.mvi

interface MviReducer<State: MviState, PartialState: MviPartialState> {
    fun reduce(prevState: State, partialState: PartialState): State
}