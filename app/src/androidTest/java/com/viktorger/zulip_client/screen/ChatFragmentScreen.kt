package com.viktorger.zulip_client.screen

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.kaspersky.kaspresso.screens.KScreen
import com.viktorger.zulip_client.app.R
import com.viktorger.zulip_client.app.presentation.chat.ChatFragment
import com.viktorger.zulip_client.custom_view.KEmojiView
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.progress.KProgressBar
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import io.github.kakaocup.kakao.toolbar.KToolbar
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher

object ChatFragmentScreen : KScreen<ChatFragmentScreen>() {
    override val layoutId: Int = R.layout.fragment_chat
    override val viewClass: Class<*> = ChatFragment::class.java

    val recycler = KRecyclerView(
        builder = { withId(R.id.rvChat) },
        itemTypeBuilder = {
            itemType(::KSentMessageItem)
            itemType(::KReceivedMessageItem)
        }
    )
    val progressBar = KProgressBar { withId(R.id.pbChat) }

    class KSentMessageItem(
        parent: Matcher<View>
    ) : KRecyclerItem<KSentMessageItem>(allOf(parent, withId(R.id.omlMessageSent))) {
        val firstReaction = KEmojiView {
            isDescendantOfA {
                withId(R.id.fblMessageSent)
                isDescendantOfA { withMatcher(parent) }
            }
            onPosition(0)
        }
    }

    class KReceivedMessageItem(
        parent: Matcher<View>
    ) : KRecyclerItem<KReceivedMessageItem>(allOf(parent, withId(R.id.mlMessageReceived))) {

    }

    val toolbar = KToolbar { withId(R.id.tbChat) }
    val topicName = KTextView { withId(R.id.tvChatTopic) }

    val messageField = KEditText { withId(R.id.etChat) }
    val actionButton = KButton { withId(R.id.ibChatAction) }
}