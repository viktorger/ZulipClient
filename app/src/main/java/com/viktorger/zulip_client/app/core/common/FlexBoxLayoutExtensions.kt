package com.viktorger.zulip_client.app.core.common

import android.view.LayoutInflater
import android.widget.ImageButton
import com.viktorger.zulip_client.app.R
import com.viktorger.zulip_client.app.core.ui.custom_view.EmojiView
import com.viktorger.zulip_client.app.core.ui.custom_view.FlexBoxLayout

internal fun FlexBoxLayout.addEmojiView(): EmojiView {
    LayoutInflater.from(context).inflate(R.layout.item_emoji_view, this)
    return getChildAt(childCount - 1) as EmojiView
}
internal fun FlexBoxLayout.addAddButton(): ImageButton {
    LayoutInflater.from(context).inflate(R.layout.item_add, this)
    return getChildAt(childCount - 1) as ImageButton
}