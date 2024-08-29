package com.viktorger.zulip_client.app.core.shared_preferences

interface SharedPreferencesDataSource {
    suspend fun getUserId(): Int
    suspend fun saveUserId(userId: Int)
}