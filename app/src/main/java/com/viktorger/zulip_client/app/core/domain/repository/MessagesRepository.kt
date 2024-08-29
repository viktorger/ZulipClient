package com.viktorger.zulip_client.app.core.domain.repository

import com.viktorger.zulip_client.app.core.model.EventModel
import com.viktorger.zulip_client.app.core.model.InitialChatRequest
import com.viktorger.zulip_client.app.core.model.LoadMessagePageRequest
import com.viktorger.zulip_client.app.core.model.MessageModel
import com.viktorger.zulip_client.app.core.model.RegistrationAnswer
import com.viktorger.zulip_client.app.core.model.SendMessageParams
import kotlinx.coroutines.flow.Flow

interface MessagesRepository {
    fun getSavedChatAndUpdate(initialChatRequest: InitialChatRequest): Flow<List<MessageModel>>

    suspend fun getNextEvents(queueId: String, lastEventId: Int): List<EventModel>
    suspend fun registerEventQueue(streamName: String, topicName: String): RegistrationAnswer
    suspend fun deleteEventQueue(queueId: String)
    suspend fun loadPageBeforeId(loadPageRequest: LoadMessagePageRequest): List<MessageModel>
    suspend fun loadPageAfterId(loadPageRequest: LoadMessagePageRequest): List<MessageModel>
    suspend fun sendChatMessage(sendMessageParams: SendMessageParams)
    suspend fun addReaction(messageId: Int, emoji: String)
    suspend fun removeReaction(messageId: Int, emoji: String)
}