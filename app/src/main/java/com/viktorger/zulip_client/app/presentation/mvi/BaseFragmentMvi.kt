package com.viktorger.zulip_client.app.presentation.mvi

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.withStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

abstract class BaseFragmentMvi<
        PartialState : MviPartialState,
        Intent : MviIntent,
        State : MviState,
        Effect : MviEffect> : Fragment() {
    protected abstract val store: MviStore<PartialState, Intent, State, Effect>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store.uiState.onEach(::renderOnStarted).launchIn(lifecycleScope)
        store.effect.onEach(::resolveEffectOnStarted).launchIn(lifecycleScope)
    }

    private fun renderOnStarted(state: State) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                render(state)
            }
        }
    }

    private fun resolveEffectOnStarted(effect: Effect) {
        lifecycleScope.launch {
            withStarted {
                resolveEffect(effect)
            }
        }
    }

    protected abstract fun render(state: State)
    protected abstract fun resolveEffect(effect: Effect)
}