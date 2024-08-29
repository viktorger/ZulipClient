package com.viktorger.zulip_client.app.di.component

import com.viktorger.zulip_client.app.di.scope.FragmentScope
import com.viktorger.zulip_client.app.presentation.chat.ChatFragment
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class])
interface ChatComponent {
    fun inject(chatFragment: ChatFragment)
    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): ChatComponent
    }
}