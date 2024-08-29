package com.viktorger.zulip_client.custom_view

import android.view.View
import com.viktorger.zulip_client.app.core.ui.custom_view.EmojiView
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class ReactionMatcher(private val emoji: String): TypeSafeMatcher<View>() {
    override fun describeTo(description: Description) {
        description.appendText("with emoji $emoji")
    }

    override fun matchesSafely(item: View): Boolean {
        if (item is EmojiView) {
            return item.emoji == emoji
        }
        return false
    }
}

class ReactionCountMatcher(private val count: Int): TypeSafeMatcher<View>() {
    override fun describeTo(description: Description) {
        description.appendText("with count $count")
    }

    override fun matchesSafely(item: View): Boolean {
        if (item is EmojiView) {
            return item.count == count
        }
        return false
    }
}