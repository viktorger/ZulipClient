package com.viktorger.zulip_client.app.core.data.repository

import com.viktorger.zulip_client.app.core.common.ANCHOR_NEWEST
import com.viktorger.zulip_client.app.core.common.DB_MESSAGES_CAPACITY
import com.viktorger.zulip_client.app.core.common.NO_MESSAGES
import com.viktorger.zulip_client.app.core.common.PAGE_SIZE
import com.viktorger.zulip_client.app.core.common.USER_ID_NOT_FOUND
import com.viktorger.zulip_client.app.core.common.getEmojiByUnicode
import com.viktorger.zulip_client.app.core.common.textFromHtml
import com.viktorger.zulip_client.app.core.common.toMillis
import com.viktorger.zulip_client.app.core.data.data_source.EmojiListDataSource
import com.viktorger.zulip_client.app.core.data.model.mapping.messageModelsNetworkToExternal
import com.viktorger.zulip_client.app.core.data.model.mapping.messagesWithReactionsToExternal
import com.viktorger.zulip_client.app.core.data.model.mapping.networkEventsToExternal
import com.viktorger.zulip_client.app.core.data.model.mapping.reactionModelsNetworkToEntities
import com.viktorger.zulip_client.app.core.database.dao.MessageDao
import com.viktorger.zulip_client.app.core.database.dao.ReactionDao
import com.viktorger.zulip_client.app.core.database.entities.MessageEntity
import com.viktorger.zulip_client.app.core.database.entities.ReactionEntity
import com.viktorger.zulip_client.app.core.database.relationship.MessageWithReactions
import com.viktorger.zulip_client.app.core.domain.repository.MessagesRepository
import com.viktorger.zulip_client.app.core.model.EventModel
import com.viktorger.zulip_client.app.core.model.InitialChatRequest
import com.viktorger.zulip_client.app.core.model.LoadMessagePageRequest
import com.viktorger.zulip_client.app.core.model.MessageModel
import com.viktorger.zulip_client.app.core.model.RegistrationAnswer
import com.viktorger.zulip_client.app.core.model.SendMessageParams
import com.viktorger.zulip_client.app.core.network.ZulipApi
import com.viktorger.zulip_client.app.core.network.model.MessageModelNetwork
import com.viktorger.zulip_client.app.core.shared_preferences.SharedPreferencesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MessagesRepositoryImpl @Inject constructor(
    private val messageDao: MessageDao,
    private val reactionDao: ReactionDao,
    private val zulipApi: ZulipApi,
    private val sharedPreferencesDataSource: SharedPreferencesDataSource,
    private val emojiListDataSource: EmojiListDataSource
) : MessagesRepository {
    override fun getSavedChatAndUpdate(
        initialChatRequest: InitialChatRequest
    ): Flow<List<MessageModel>> = flow {
        val cachedMessages = messageDao.getMessagesWithReaction(
            initialChatRequest.streamName,
            initialChatRequest.topicName
        )
        if (cachedMessages.isNotEmpty()) {
            emit(messagesWithReactionsToExternal(cachedMessages))
        }

        val downloadedMessaged = if (cachedMessages.isEmpty()) {
            loadNewestToInitChat(initialChatRequest)
        } else {
            loadToUpdateCached(initialChatRequest, cachedMessages)
        }
        val userId = getUserId()
        emit(messageModelsNetworkToExternal(downloadedMessaged, userId))

        updateCachedMessages(downloadedMessaged, initialChatRequest, userId)
    }

    override suspend fun addReaction(messageId: Int, emoji: String) {
        if (emojiListDataSource.getEmojiList() == null) {
            initializeEmojiList()
        }

        getEmojiUnicode(emoji)?.let {
            zulipApi.addReaction(messageId, it)
        }
    }

    override suspend fun removeReaction(messageId: Int, emoji: String) {
        if (emojiListDataSource.getEmojiList() == null) {
            initializeEmojiList()
        }

        getEmojiUnicode(emoji)?.let {
            zulipApi.removeReaction(messageId, it)
        }
    }

    override suspend fun sendChatMessage(sendMessageParams: SendMessageParams) =
        zulipApi.sendMessage(
            streamName = sendMessageParams.streamName,
            topicName = sendMessageParams.topicName,
            content = sendMessageParams.content,
        )

    override suspend fun getNextEvents(queueId: String, lastEventId: Int): List<EventModel> {
        val events = zulipApi.getMessagesEvent(queueId, lastEventId).events
        val userId = getUserId()

        val eventModelList: List<EventModel> = networkEventsToExternal(events, userId)

        return eventModelList
    }


    override suspend fun registerEventQueue(
        streamName: String, topicName: String
    ): RegistrationAnswer {
        val registerResponse = zulipApi.registerEventQueue(
            ZulipApi.buildMessagesEventsNarrow(
                streamName,
                topicName
            )
        )

        return RegistrationAnswer(
            queueId = registerResponse.queueId,
            lastEventId = registerResponse.lastEventId
        )
    }

    override suspend fun deleteEventQueue(queueId: String) {
        zulipApi.deleteEventQueue(queueId)
    }

    override suspend fun loadPageBeforeId(
        loadPageRequest: LoadMessagePageRequest
    ): List<MessageModel> {
        val downloadedMessaged = zulipApi.getMessages(
            narrowObject = ZulipApi.buildMessagesNarrow(
                loadPageRequest.streamName, loadPageRequest.topicName
            ),
            numBefore = loadPageRequest.pageSize,
            numAfter = 0,
            anchor = loadPageRequest.messageId.toString(),
            includeAnchor = false
        ).messages
        val userId = getUserId()

        saveOldDataIfNeeded(
            loadPageRequest.streamName,
            loadPageRequest.topicName,
            downloadedMessaged,
            userId,
            loadPageRequest.messageId
        )

        return messageModelsNetworkToExternal(downloadedMessaged, userId)
    }

    private fun updateCachedMessages(
        downloadedMessaged: List<MessageModelNetwork>,
        initialChatRequest: InitialChatRequest,
        userId: Int
    ) {
        val messages = networkMessagesToEntities(
            downloadedMessaged,
            initialChatRequest.streamName,
            initialChatRequest.topicName
        )
        val reactions = reactionEntitiesFromNetworkMessages(downloadedMessaged, userId)

        messageDao.updateMessages(
            messages,
            initialChatRequest.streamName,
            initialChatRequest.topicName
        )
        reactionDao.updateReactions(reactions)
    }

    private suspend fun getUserId(): Int {
        var userId = sharedPreferencesDataSource.getUserId()
        if (userId == USER_ID_NOT_FOUND) {
            userId = zulipApi.getOwnProfile().userId
            sharedPreferencesDataSource.saveUserId(userId)
        }

        return userId
    }

    private suspend fun loadToUpdateCached(
        initialChatRequest: InitialChatRequest,
        cachedMessages: List<MessageWithReactions>
    ) = zulipApi.getMessages(
        narrowObject = ZulipApi.buildMessagesNarrow(
            initialChatRequest.streamName,
            initialChatRequest.topicName
        ),
        numBefore = cachedMessages.size,
        numAfter = 0,
        anchor = cachedMessages.last().message.id.toString()
    ).messages

    private suspend fun loadNewestToInitChat(initialChatRequest: InitialChatRequest) =
        zulipApi.getMessages(
            narrowObject = ZulipApi.buildMessagesNarrow(
                initialChatRequest.streamName,
                initialChatRequest.topicName
            ),
            numBefore = initialChatRequest.pageSize,
            numAfter = 0,
            anchor = ANCHOR_NEWEST
        ).messages

    override suspend fun loadPageAfterId(
        loadPageRequest: LoadMessagePageRequest
    ): List<MessageModel> {
        val downloadedMessaged = if (loadPageRequest.messageId == NO_MESSAGES) {
            listOf()
        } else {
            zulipApi.getMessages(
                narrowObject = ZulipApi.buildMessagesNarrow(
                    loadPageRequest.streamName, loadPageRequest.topicName
                ),
                numBefore = 0,
                numAfter = loadPageRequest.pageSize,
                anchor = loadPageRequest.messageId.toString(),
                includeAnchor = false
            ).messages
        }
        val userId = getUserId()

        saveLatestData(
            loadPageRequest.streamName,
            loadPageRequest.topicName,
            downloadedMessaged,
            userId
        )

        return messageModelsNetworkToExternal(downloadedMessaged, userId)
    }

    private fun networkMessagesToEntities(
        downloadedMessaged: List<MessageModelNetwork>,
        streamName: String,
        topicName: String
    ): List<MessageEntity> = downloadedMessaged.map {
        MessageEntity(
            id = it.messageId,
            userId = it.userId,
            username = it.username,
            timestamp = toMillis(it.timestamp),
            content = textFromHtml(it.content),
            avatarUrl = it.avatarUrl,
            topicName = topicName,
            streamName = streamName
        )
    }

    private fun saveOldDataIfNeeded(
        streamName: String,
        topicName: String,
        messages: List<MessageModelNetwork>,
        userId: Int,
        messageId: Int
    ) {
        if (messageId != messageDao.getMinMessageIdInChat(streamName, topicName)) {
            return
        }

        val cachedMessagesCount = messageDao.getMessagesCount(streamName, topicName)

        val neededMessagesCount = when {
            cachedMessagesCount < DB_MESSAGES_CAPACITY - PAGE_SIZE -> messages.size
            DB_MESSAGES_CAPACITY - cachedMessagesCount < messages.size -> {
                messages.size - (DB_MESSAGES_CAPACITY - cachedMessagesCount)
            }

            else -> {
                messages.size
            }
        }
        val neededMessages = messages.subList(messages.size - neededMessagesCount, messages.size)

        saveNetworkMessages(streamName, topicName, neededMessages, userId)
    }

    private fun saveLatestData(
        streamName: String,
        topicName: String,
        messages: List<MessageModelNetwork>,
        userId: Int
    ) {
        saveNetworkMessages(streamName, topicName, messages, userId)
        removeExcessiveDbEntries(streamName, topicName)
    }

    private fun saveNetworkMessages(
        streamName: String,
        topicName: String,
        messages: List<MessageModelNetwork>,
        userId: Int
    ) {
        val messageEntities = networkMessagesToEntities(messages, streamName, topicName)
        val reactionEntities = reactionEntitiesFromNetworkMessages(messages, userId)

        messageDao.insertMessages(messageEntities)
        reactionDao.insertReactions(reactionEntities)
    }

    private fun removeExcessiveDbEntries(streamName: String, topicName: String) {
        val messagesIds = messageDao.getMessagesIds(streamName, topicName)
        if (messagesIds.size > DB_MESSAGES_CAPACITY) {
            val messagesToDelete = messagesIds.subList(DB_MESSAGES_CAPACITY, messagesIds.size)
            messageDao.deleteMessagesByIds(messagesToDelete)
            reactionDao.deleteReactionsByMessagesIds(messagesIds)
        }
    }

    private fun reactionEntitiesFromNetworkMessages(
        downloadedMessaged: List<MessageModelNetwork>,
        userId: Int
    ): List<ReactionEntity> {
        val reactions = mutableListOf<ReactionEntity>()
        downloadedMessaged.forEach {
            reactions.addAll(reactionModelsNetworkToEntities(it.reactions, it.messageId, userId))
        }
        return reactions
    }


    // EmojiList is initialized by the time
    private fun getEmojiUnicode(emoji: String): String? = emojiListDataSource.getEmojiList()!![emoji]

    private suspend fun initializeEmojiList() {
        emojiListDataSource.saveEmojiList(
            zulipApi.getEmojiList().codepointToName.mapKeys {
                getEmojiByUnicode(
                    it.key
                )
            }
        )
    }
}