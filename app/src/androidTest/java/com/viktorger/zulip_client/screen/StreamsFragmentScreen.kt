package com.viktorger.zulip_client.screen

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.kaspersky.kaspresso.screens.KScreen
import com.viktorger.zulip_client.app.R
import com.viktorger.zulip_client.app.presentation.streams.StreamsFragment
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.text.KTextView
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher

object StreamsFragmentScreen : KScreen<StreamsFragmentScreen>() {
    override val layoutId: Int = R.layout.fragment_channels
    override val viewClass: Class<*> = StreamsFragment::class.java

    val recycler = KRecyclerView(
        builder = { withId(R.id.rvChannelsTab) },
        itemTypeBuilder = {
            itemType(::KStreamItem)
            itemType(::KTopicItem)
            itemType(::KStreamShimmerItem)
        }
    )

    class KStreamItem(
        parent: Matcher<View>
    ) : KRecyclerItem<KStreamItem>(allOf(parent, withId(R.id.stream))) {
        val streamName = KTextView {withId(R.id.tvStreamStream)}
    }

    class KTopicItem(
        parent: Matcher<View>
    ) : KRecyclerItem<KTopicItem>(allOf(parent, withId(R.id.topic))) {
        val name = KTextView {withId(R.id.tvTopicName)}
    }

    class KStreamShimmerItem(
        parent: Matcher<View>
    ) : KRecyclerItem<KStreamShimmerItem>(allOf(parent, withId(R.id.streamShimmer)))

}