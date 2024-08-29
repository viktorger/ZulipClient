package com.viktorger.zulip_client.app.presentation.navigation

sealed class TaggedScreens(val tag: String) {
    data object Channels : TaggedScreens(TAG_FRAGMENT_CHANNELS)
    data object People : TaggedScreens(TAG_FRAGMENT_PEOPLE)
    data object Profile : TaggedScreens(TAG_FRAGMENT_PROFILE)

    companion object {
        private const val TAG_FRAGMENT_CHANNELS = "fragment_channels"
        private const val TAG_FRAGMENT_PEOPLE = "fragment_people"
        private const val TAG_FRAGMENT_PROFILE = "fragment_profile"
    }
}