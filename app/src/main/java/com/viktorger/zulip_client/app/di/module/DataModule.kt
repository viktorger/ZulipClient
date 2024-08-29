package com.viktorger.zulip_client.app.di.module

import com.viktorger.zulip_client.app.core.data.data_source.EmojiListDataSource
import com.viktorger.zulip_client.app.core.data.data_source.EmojiListDataSourceImpl
import com.viktorger.zulip_client.app.core.domain.repository.MessagesRepository
import com.viktorger.zulip_client.app.core.data.repository.MessagesRepositoryImpl
import com.viktorger.zulip_client.app.core.domain.repository.ProfileRepository
import com.viktorger.zulip_client.app.core.data.repository.ProfileRepositoryImpl
import com.viktorger.zulip_client.app.core.domain.repository.StreamsRepository
import com.viktorger.zulip_client.app.core.data.repository.StreamsRepositoryImpl
import com.viktorger.zulip_client.app.core.domain.repository.UserRepository
import com.viktorger.zulip_client.app.core.data.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
interface DataModule {
    @Binds
    fun bindMessagesRepository(messagesRepositoryImpl: MessagesRepositoryImpl): MessagesRepository

    @Binds
    fun bindProfileRepository(profileRepositoryImpl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    fun bindStreamsRepository(streamsRepositoryImpl: StreamsRepositoryImpl): StreamsRepository

    @Binds
    fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    fun bindEmojiListDataSource(emojiListDataSourceImpl: EmojiListDataSourceImpl): EmojiListDataSource
}