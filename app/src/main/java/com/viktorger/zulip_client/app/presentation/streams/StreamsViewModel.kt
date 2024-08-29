package com.viktorger.zulip_client.app.presentation.streams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn

class StreamsViewModel : ViewModel() {
    val searchQueryPublisher = MutableStateFlow("")

    init {
        searchQueryPublisher.launchIn(viewModelScope)
    }
}