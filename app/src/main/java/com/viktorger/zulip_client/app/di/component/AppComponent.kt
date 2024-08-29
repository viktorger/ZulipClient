package com.viktorger.zulip_client.app.di.component

import android.content.Context
import com.viktorger.zulip_client.app.core.domain.repository.MessagesRepository
import com.viktorger.zulip_client.app.core.domain.repository.ProfileRepository
import com.viktorger.zulip_client.app.core.domain.repository.StreamsRepository
import com.viktorger.zulip_client.app.core.domain.repository.UserRepository
import com.viktorger.zulip_client.app.di.module.DataModule
import com.viktorger.zulip_client.app.di.module.DatabaseModule
import com.viktorger.zulip_client.app.di.module.NetworkModule
import com.viktorger.zulip_client.app.di.module.SharedPreferencesModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    DataModule::class,
    NetworkModule::class,
    DatabaseModule::class,
    SharedPreferencesModule::class
])
interface AppComponent {
    val streamsRepository: StreamsRepository
    val userRepository: UserRepository
    val profileRepository: ProfileRepository
    val messagesRepository: MessagesRepository

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}