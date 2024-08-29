package com.viktorger.zulip_client.custom_view

import androidx.test.espresso.assertion.ViewAssertions
import io.github.kakaocup.kakao.common.assertions.BaseAssertions

interface EmojiViewAssertions : BaseAssertions {
    fun hasReaction(emoji: String) {
        view.check(
            ViewAssertions.matches(
                ReactionMatcher(emoji)
            )
        )
    }

    fun hasCount(count: Int) {
        view.check(
            ViewAssertions.matches(
                ReactionCountMatcher(count)
            )
        )
    }
}