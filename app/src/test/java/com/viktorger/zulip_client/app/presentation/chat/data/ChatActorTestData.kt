package com.viktorger.zulip_client.app.presentation.chat.data

import com.viktorger.zulip_client.app.core.data.repository.fake.FakeMessagesRepository
import com.viktorger.zulip_client.app.core.data.repository.fake.FakeProfileRepository
import com.viktorger.zulip_client.app.core.data.repository.fake.FakeUnsuccessfulMessagesRepository
import com.viktorger.zulip_client.app.core.domain.use_case.GetNextEventsUseCase
import com.viktorger.zulip_client.app.core.domain.use_case.GetSavedChatUseCase
import com.viktorger.zulip_client.app.core.domain.use_case.LoadPageAfterIdUseCase
import com.viktorger.zulip_client.app.core.domain.use_case.LoadPageBeforeIdUseCase
import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem
import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.presentation.chat.ChatActor
import com.viktorger.zulip_client.app.presentation.chat.ChatState

class ChatActorTestData {
    fun createSuccessfulActor(): ChatActor {
        val messagesRepository = FakeMessagesRepository()
        val profileRepository = FakeProfileRepository()
        return ChatActor(
            messagesRepository = messagesRepository,
            getSavedChatUseCase = GetSavedChatUseCase(
                messagesRepository = messagesRepository,
                profileRepository = profileRepository
            ),
            loadPageAfterIdUseCase = LoadPageAfterIdUseCase(
                messagesRepository = messagesRepository,
                profileRepository = profileRepository
            ),
            loadPageBeforeIdUseCase = LoadPageBeforeIdUseCase(
                messagesRepository = messagesRepository,
                profileRepository = profileRepository
            ),
            getNextEventsUseCase = GetNextEventsUseCase(
                messagesRepository = messagesRepository,
                profileRepository = profileRepository
            )
        )
    }

    fun createUnsuccessfulActor(): ChatActor {
        val messagesRepository = FakeUnsuccessfulMessagesRepository()
        val profileRepository = FakeProfileRepository()
        return ChatActor(
            messagesRepository = messagesRepository,
            getSavedChatUseCase = GetSavedChatUseCase(
                messagesRepository = messagesRepository,
                profileRepository = profileRepository
            ),
            loadPageAfterIdUseCase = LoadPageAfterIdUseCase(
                messagesRepository = messagesRepository,
                profileRepository = profileRepository
            ),
            loadPageBeforeIdUseCase = LoadPageBeforeIdUseCase(
                messagesRepository = messagesRepository,
                profileRepository = profileRepository
            ),
            getNextEventsUseCase = GetNextEventsUseCase(
                messagesRepository = messagesRepository,
                profileRepository = profileRepository
            )
        )
    }

    val stateEmptyData = ChatState(LceState.Content<List<DelegateItem>>(emptyList()))
}