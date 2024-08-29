package com.viktorger.zulip_client.app.core.data.repository.fake

import com.viktorger.zulip_client.app.core.domain.repository.MessagesRepository
import com.viktorger.zulip_client.app.core.model.EventModel
import com.viktorger.zulip_client.app.core.model.InitialChatRequest
import com.viktorger.zulip_client.app.core.model.LoadMessagePageRequest
import com.viktorger.zulip_client.app.core.model.MessageModel
import com.viktorger.zulip_client.app.core.model.RegistrationAnswer
import com.viktorger.zulip_client.app.core.model.SendMessageParams
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException

class FakeUnsuccessfulMessagesRepository : MessagesRepository {
    override fun getSavedChatAndUpdate(initialChatRequest: InitialChatRequest): Flow<List<MessageModel>> =
        flow {
            throw IOException()
        }

    override suspend fun getNextEvents(queueId: String, lastEventId: Int): List<EventModel> {
        throw IOException()
    }

    override suspend fun registerEventQueue(
        streamName: String,
        topicName: String
    ): RegistrationAnswer {
        throw IOException()
    }

    override suspend fun deleteEventQueue(queueId: String) {
        throw IOException()
    }

    override suspend fun loadPageBeforeId(loadPageRequest: LoadMessagePageRequest): List<MessageModel> {
        throw IOException()
    }

    override suspend fun loadPageAfterId(loadPageRequest: LoadMessagePageRequest): List<MessageModel> {
        throw IOException()
    }

    override suspend fun sendChatMessage(sendMessageParams: SendMessageParams) {
        throw IOException()
    }

    override suspend fun addReaction(messageId: Int, emoji: String) {
        throw IOException()
    }

    override suspend fun removeReaction(messageId: Int, emoji: String) {
        throw IOException()
    }
}

