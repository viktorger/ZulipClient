package com.viktorger.zulip_client.mock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.viktorger.zulip_client.util.AssetsUtil

class MockUnreadMessages(private val wireMockServer: WireMockServer) {
    private val matcher = WireMock.get(urlPattern)

    fun withUnreadMessages() {
        wireMockServer.stubFor(
            matcher
                .willReturn(WireMock.ok(AssetsUtil.fromAssets("chat/allMessages.json")))
        )
    }

    companion object {
        val urlPattern = WireMock.urlMatching("/messages.*narrow=.*num_before=0&num_after=1000&anchor=first_unread")
        fun WireMockServer.unreadMessages(block: MockUnreadMessages.() -> Unit) {
            MockUnreadMessages(this).apply(block)
        }
    }
}