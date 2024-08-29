package com.viktorger.zulip_client.app.di.component

import com.viktorger.zulip_client.app.di.scope.FragmentScope
import com.viktorger.zulip_client.app.presentation.people.PeopleFragment
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class])
interface PeopleComponent {
    fun inject(peopleFragment: PeopleFragment)
    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): PeopleComponent
    }
}