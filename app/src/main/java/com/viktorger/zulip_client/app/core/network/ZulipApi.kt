package com.viktorger.zulip_client.app.core.network

import com.viktorger.zulip_client.app.core.network.model.EmojiListResponse
import com.viktorger.zulip_client.app.core.network.model.EventResponse
import com.viktorger.zulip_client.app.core.network.model.MessagesEmptyList
import com.viktorger.zulip_client.app.core.network.model.MessagesList
import com.viktorger.zulip_client.app.core.network.model.OwnProfileNetwork
import com.viktorger.zulip_client.app.core.network.model.PresenceResponse
import com.viktorger.zulip_client.app.core.network.model.ProfileResponse
import com.viktorger.zulip_client.app.core.network.model.RegisterResponse
import com.viktorger.zulip_client.app.core.network.model.StreamIdResponse
import com.viktorger.zulip_client.app.core.network.model.StreamList
import com.viktorger.zulip_client.app.core.network.model.SubscriptionsList
import com.viktorger.zulip_client.app.core.network.model.TopicList
import com.viktorger.zulip_client.app.core.network.model.UsersList
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ZulipApi {
    @GET("streams")
    suspend fun getAllStreams(): StreamList

    @GET("users/me/{streamId}/topics")
    suspend fun getTopicsInStream(
        @Path("streamId") streamId: Int
    ): TopicList

    @GET("get_stream_id")
    suspend fun getStreamId(
        @Query("stream") streamName: String
    ): StreamIdResponse

    @GET("users/me/subscriptions")
    suspend fun getSubscribedStreams(): SubscriptionsList

    @GET("users/me")
    suspend fun getOwnProfile(): OwnProfileNetwork

    @GET("users")
    suspend fun getAllUsers(): UsersList

    @GET("users/{userId}/presence")
    suspend fun getUserPresence(
        @Path("userId") userId: Int
    ): PresenceResponse

    @GET("users/{userId}")
    suspend fun getProfile(
        @Path("userId") userId: Int
    ): ProfileResponse

    @GET("messages")
    suspend fun getMessages(
        @Query("narrow") narrowObject: String,
        @Query("num_before") numBefore: Int = 1000,
        @Query("num_after") numAfter: Int = 1000,
        @Query("anchor") anchor: String = "first_unread",
        @Query("include_anchor") includeAnchor: Boolean = true
    ): MessagesList

    @POST("messages")
    suspend fun sendMessage(
        @Query("to") streamName: String,
        @Query("topic") topicName: String,
        @Query("content") content: String,
        @Query("type") type: String = "stream",
    )

    @POST("register")
    suspend fun registerEventQueue(
        @Query("narrow") narrow: String,
        @Query("event_types") eventTypes: String = "[\"message\",\"reaction\"]",
        @Query("apply_markdown") applyMarkdown: Boolean = true
    ): RegisterResponse

    @DELETE("events?queue_id")
    suspend fun deleteEventQueue(
        @Query("queue_id") queueId: String
    )

    @GET("events")
    suspend fun getMessagesEvent(
        @Query("queue_id") queueId: String,
        @Query("last_event_id") lastEventId: Int
    ): EventResponse


    @GET("/static/generated/emoji/emoji_codes.json")
    suspend fun getEmojiList(): EmojiListResponse

    @POST("messages/{messageId}/reactions")
    suspend fun addReaction(
        @Path("messageId") messageId: Int,
        @Query("emoji_name") emojiName: String
    )

    @DELETE("messages/{messageId}/reactions")
    suspend fun removeReaction(
        @Path("messageId") messageId: Int,
        @Query("emoji_name") emojiName: String
    )

    @GET("messages")
    suspend fun getTopicUnreadMessages(
        @Query("narrow") narrowObject: String,
        @Query("num_before") numBefore: Int = 0,
        @Query("num_after") numAfter: Int = 1000,
        @Query("anchor") anchor: String = "first_unread"
    ): MessagesEmptyList

    companion object {
        fun buildMessagesNarrow(streamName: String, topicName: String) =
            "[{\"operator\": \"stream\", \"operand\": \"$streamName\"},{\"operator\": \"topic\", \"operand\": \"$topicName\"}]"

        fun buildMessagesEventsNarrow(streamName: String, topicName: String) =
            "[[\"stream\", \"$streamName\"],[\"topic\", \"$topicName\"]]"
    }
}