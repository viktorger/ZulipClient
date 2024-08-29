package com.viktorger.zulip_client.app.presentation.app

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import com.github.terrakok.cicerone.Cicerone
import com.viktorger.zulip_client.app.di.component.AppComponent
import com.viktorger.zulip_client.app.di.component.DaggerAppComponent

class ZulipApplication : Application() {
    private val cicerone = Cicerone.create()
    val router get() = cicerone.router
    val navigatorHolder get() = cicerone.getNavigatorHolder()
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        appComponent = DaggerAppComponent.factory().create(this)
    }

    companion object {
        lateinit var INSTANCE: ZulipApplication
    }
}

internal val Context.appComponent get() = (applicationContext as ZulipApplication).appComponent
internal val Fragment.appComponent get() = requireContext().appComponent