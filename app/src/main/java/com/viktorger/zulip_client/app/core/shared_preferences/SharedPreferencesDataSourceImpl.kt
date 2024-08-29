package com.viktorger.zulip_client.app.core.shared_preferences

import android.content.SharedPreferences
import com.viktorger.zulip_client.app.core.common.USER_ID_NOT_FOUND
import javax.inject.Inject

class SharedPreferencesDataSourceImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : SharedPreferencesDataSource {

    override suspend fun getUserId(): Int = sharedPreferences.getInt(USER_ID, USER_ID_NOT_FOUND)

    override suspend fun saveUserId(userId: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(USER_ID, userId)
        editor.apply()
    }

    companion object {
        const val USER_ID = "user_id"
    }
}