package com.viktorger.zulip_client.app.di.component

import com.viktorger.zulip_client.app.di.scope.FragmentScope
import com.viktorger.zulip_client.app.presentation.profile.ProfileFragment
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class])
interface ProfileComponent {
    fun inject(profileFragment: ProfileFragment)

    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): ProfileComponent
    }
}