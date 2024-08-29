package com.viktorger.zulip_client.mock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.viktorger.zulip_client.util.AssetsUtil

class MockReactionList(private val wireMockServer: WireMockServer) {
    private val matcher = WireMock.get(urlPattern)

    fun withReactionList() {
        wireMockServer.stubFor(
            matcher
                .willReturn(WireMock.ok(AssetsUtil.fromAssets("chat/emojiList.json")))
        )
    }

    companion object {
        val urlPattern = WireMock.urlPathMatching("/static/generated/emoji/emoji_codes.json")
        fun WireMockServer.reactionList(block: MockReactionList.() -> Unit) {
            MockReactionList(this).apply(block)
        }
    }
}