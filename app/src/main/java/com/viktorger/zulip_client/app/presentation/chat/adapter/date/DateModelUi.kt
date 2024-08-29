package com.viktorger.zulip_client.app.presentation.chat.adapter.date

data class DateModelUi (
    val year: String,
    val date: String
) {
    fun getFullDate() = "$year $date"
}