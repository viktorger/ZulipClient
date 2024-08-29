package com.viktorger.zulip_client.app.core.domain.use_case

import com.viktorger.zulip_client.app.core.domain.repository.MessagesRepository
import com.viktorger.zulip_client.app.core.domain.repository.ProfileRepository
import com.viktorger.zulip_client.app.core.model.ChatModel
import com.viktorger.zulip_client.app.core.model.InitialChatRequest
import com.viktorger.zulip_client.app.core.model.chatModelFromMessageModels
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetSavedChatUseCase @Inject constructor(
    private val messagesRepository: MessagesRepository,
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(initialChatRequest: InitialChatRequest): Flow<ChatModel> {
        var userId = 0

        return messagesRepository.getSavedChatAndUpdate(initialChatRequest)
            .onStart { userId = profileRepository.getOwnProfileUserId() }
            .map { chatModelFromMessageModels(it, userId) }
    }
}