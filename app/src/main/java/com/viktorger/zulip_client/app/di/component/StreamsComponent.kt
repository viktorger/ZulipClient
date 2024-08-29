package com.viktorger.zulip_client.app.di.component

import com.viktorger.zulip_client.app.di.scope.FragmentScope
import com.viktorger.zulip_client.app.presentation.streams.tab.StreamsTabFragment
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class])
interface StreamsComponent {
    fun inject(streamsTabFragment: StreamsTabFragment)

    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): StreamsComponent
    }
}