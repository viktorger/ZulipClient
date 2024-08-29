package com.viktorger.zulip_client.app.core.domain.use_case

import com.viktorger.zulip_client.app.core.domain.repository.MessagesRepository
import com.viktorger.zulip_client.app.core.domain.repository.ProfileRepository
import com.viktorger.zulip_client.app.core.model.EventSeparatedMessageModel
import javax.inject.Inject

class GetNextEventsUseCase @Inject constructor(
    private val messagesRepository: MessagesRepository,
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(
        queueId: String, lastEventId: Int
    ): List<EventSeparatedMessageModel> {
        val events = messagesRepository.getNextEvents(queueId, lastEventId)
        val userId = profileRepository.getOwnProfileUserId()

        val eventsSeparated = events.map {
            if (it.message == null) {
                EventSeparatedMessageModel(
                    eventId = it.eventId,
                    reaction = it.reaction,
                    sentMessage = null,
                    receivedMessage = null
                )
            } else {
                EventSeparatedMessageModel(
                    eventId = it.eventId,
                    reaction = it.reaction,
                    sentMessage = if (it.message.userId == userId) {
                        it.message.toSentModel()
                    } else {
                        null
                    },
                    receivedMessage = if (it.message.userId == userId) {
                        it.message.toReceivedModel()
                    } else {
                        null
                    }
                )
            }
        }
        return eventsSeparated
    }
}