package com.viktorger.zulip_client.app.di.module

import android.content.Context
import androidx.room.Room
import com.viktorger.zulip_client.app.core.database.MessengerDatabase
import dagger.Module
import dagger.Provides

@Module
object DatabaseModule {

    @Provides
    fun provideStreamDao(messengerDatabase: MessengerDatabase) = messengerDatabase.streamDao()

    @Provides
    fun provideTopicDao(messengerDatabase: MessengerDatabase) = messengerDatabase.topicDao()

    @Provides
    fun provideMessageDao(messengerDatabase: MessengerDatabase) = messengerDatabase.messageDao()

    @Provides
    fun provideReactionDao(messengerDatabase: MessengerDatabase) = messengerDatabase.reactionDao()

    @Provides
    fun provideUserDao(messengerDatabase: MessengerDatabase) = messengerDatabase.userDao()

    @Provides
    fun provideMessengerDatabase(context: Context): MessengerDatabase = Room.databaseBuilder(
            context,
            MessengerDatabase::class.java, "messenger"
        ).build()
}