package com.viktorger.zulip_client.app.presentation.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.viktorger.zulip_client.app.presentation.tabs.TabsFragment
import com.viktorger.zulip_client.app.presentation.chat.ChatFragment
import com.viktorger.zulip_client.app.presentation.foreign_profile.ForeignProfileFragment
import com.viktorger.zulip_client.app.presentation.profile.ProfileFragment
import com.viktorger.zulip_client.app.presentation.streams.StreamsFragment
import com.viktorger.zulip_client.app.presentation.people.PeopleFragment

object Screens {
    private const val TAG_FRAGMENT_CHANNELS = "fragment_channels"
    private const val TAG_FRAGMENT_PEOPLE = "fragment_people"
    private const val TAG_FRAGMENT_PROFILE = "fragment_profile"

    fun Chat(streamName: String, topicName: String) =
        FragmentScreen {
            ChatFragment.newInstance(streamName, topicName)
        }

    fun Channels() = FragmentScreen(TAG_FRAGMENT_CHANNELS) {
        StreamsFragment()
    }

    fun People() = FragmentScreen(TAG_FRAGMENT_PEOPLE) {
        PeopleFragment()
    }

    fun Profile() = FragmentScreen(TAG_FRAGMENT_PROFILE) {
        ProfileFragment()
    }

    fun ForeignProfile(userId: Int) = FragmentScreen {
        ForeignProfileFragment.newInstance(userId)
    }

    fun TopNavigation() = FragmentScreen {
        TabsFragment()
    }
}