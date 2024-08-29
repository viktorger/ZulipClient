package com.viktorger.zulip_client.app.core.data.data_source

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmojiListDataSourceImpl @Inject constructor() : EmojiListDataSource {
    private var emojiList: Map<String, String>? = null
    override fun saveEmojiList(emojiList: Map<String, String>) {
        this.emojiList = emojiList
    }

    override fun getEmojiList(): Map<String, String>? = emojiList
}