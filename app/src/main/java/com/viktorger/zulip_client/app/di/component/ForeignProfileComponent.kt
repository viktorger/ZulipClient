package com.viktorger.zulip_client.app.di.component

import com.viktorger.zulip_client.app.di.scope.FragmentScope
import com.viktorger.zulip_client.app.presentation.foreign_profile.ForeignProfileFragment
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class])
interface ForeignProfileComponent {
    fun inject(foreignProfileFragment: ForeignProfileFragment)

    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): ForeignProfileComponent
    }
}