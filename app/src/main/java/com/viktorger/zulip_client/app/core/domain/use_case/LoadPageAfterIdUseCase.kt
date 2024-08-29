package com.viktorger.zulip_client.app.core.domain.use_case

import com.viktorger.zulip_client.app.core.domain.repository.MessagesRepository
import com.viktorger.zulip_client.app.core.domain.repository.ProfileRepository
import com.viktorger.zulip_client.app.core.model.LoadMessagePageRequest
import com.viktorger.zulip_client.app.core.model.MessagePageLoadAnswer
import com.viktorger.zulip_client.app.core.model.chatModelFromMessageModels
import javax.inject.Inject

class LoadPageAfterIdUseCase @Inject constructor(
    private val messagesRepository: MessagesRepository,
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(loadPageRequest: LoadMessagePageRequest): MessagePageLoadAnswer {
        val messages = messagesRepository.loadPageAfterId(loadPageRequest)
        val userId = profileRepository.getOwnProfileUserId()
        return MessagePageLoadAnswer(
            chat = chatModelFromMessageModels(messages, userId),
            limitReached = messages.size < loadPageRequest.pageSize
        )
    }
}