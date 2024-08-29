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

class FakeMessagesRepository : MessagesRepository {
    override fun getSavedChatAndUpdate(initialChatRequest: InitialChatRequest): Flow<List<MessageModel>> =
        flow { emit(chatModel) }

    override suspend fun getNextEvents(queueId: String, lastEventId: Int): List<EventModel> =
        listOf(
            EventModel(
                eventId = lastEventId + 1,
                message = if (lastEventId + 1 < nextMessages.lastIndex) {
                    nextMessages[lastEventId + 1]
                } else {
                    null
                }
            )
        )


    override suspend fun registerEventQueue(
        streamName: String,
        topicName: String
    ): RegistrationAnswer = RegistrationAnswer(
        queueId = "queueId",
        lastEventId = -1
    )

    override suspend fun deleteEventQueue(queueId: String) {}

    override suspend fun loadPageBeforeId(
        loadPageRequest: LoadMessagePageRequest
    ): List<MessageModel> = previousMessages

    override suspend fun loadPageAfterId(
        loadPageRequest: LoadMessagePageRequest
    ): List<MessageModel> = nextMessages

    override suspend fun sendChatMessage(sendMessageParams: SendMessageParams) {}

    override suspend fun addReaction(messageId: Int, emoji: String) {}

    override suspend fun removeReaction(messageId: Int, emoji: String) {}


    val chatModel = listOf(
        MessageModel(
            messageId = 0,
            userId = 4829,
            username = "Gabriela Trujillo",
            timestamp = 10000000000,
            content = "tation",
            avatarUrl = null,
            reactionsWithCounter = listOf()
        ),
        MessageModel(
            messageId = 1,
            timestamp = 10065600000,
            content = "audire",
            reactionsWithCounter = listOf(),
            userId = 0,
            username = "",
            avatarUrl = null
        ),
        MessageModel(
            messageId = 2,
            userId = 8755,
            username = "Arnulfo Velazquez",
            timestamp = 10152000000,
            content = "regione",
            avatarUrl = null,
            reactionsWithCounter = emptyList()
        ),
        MessageModel(
            messageId = 3,
            timestamp = 10238400000,
            content = "ea",
            reactionsWithCounter = emptyList(),
            userId = 0,
            username = "",
            avatarUrl = null
        )
    )
    val nextMessages = listOf(
        MessageModel(
            messageId = 0,
            userId = 4829,
            username = "Gabriela Trujillo",
            timestamp = 10300400000,
            content = "new tation",
            avatarUrl = null,
            reactionsWithCounter = listOf()
        ),
        MessageModel(
            messageId = 1,
            timestamp = 10350400000,
            content = "new audire",
            reactionsWithCounter = listOf(),
            userId = 0,
            username = "",
            avatarUrl = null
        ),
    )
    val previousMessages = listOf(
        MessageModel(
            messageId = 0,
            userId = 4829,
            username = "Gabriela Trujillo",
            timestamp = 25000000000,
            content = "old tation",
            avatarUrl = null,
            reactionsWithCounter = listOf()
        ),
        MessageModel(
            messageId = 1,
            timestamp = 50000000000,
            content = "old audire",
            reactionsWithCounter = listOf(),
            userId = 0,
            username = "",
            avatarUrl = null
        ),
    )
}