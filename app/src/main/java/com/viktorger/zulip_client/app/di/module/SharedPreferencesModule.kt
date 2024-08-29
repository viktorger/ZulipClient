package com.viktorger.zulip_client.app.di.module

import android.content.Context
import android.content.SharedPreferences
import com.viktorger.zulip_client.app.core.common.USER_SHARED_PREF
import com.viktorger.zulip_client.app.core.shared_preferences.SharedPreferencesDataSource
import com.viktorger.zulip_client.app.core.shared_preferences.SharedPreferencesDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface SharedPreferencesModule {
    @Binds
    fun bindSharedPreferencesDataSource(
        sharedPreferencesDataSourceImpl: SharedPreferencesDataSourceImpl
    ): SharedPreferencesDataSource
    companion object {
        @Provides
        fun provideSharedPreferences(context: Context): SharedPreferences =
            context.getSharedPreferences(USER_SHARED_PREF, Context.MODE_PRIVATE)
    }
}