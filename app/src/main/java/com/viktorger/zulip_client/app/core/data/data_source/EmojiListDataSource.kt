package com.viktorger.zulip_client.app.core.data.data_source

interface EmojiListDataSource {
    fun saveEmojiList(emojiList: Map<String, String>)
    fun getEmojiList(): Map<String, String>?
}