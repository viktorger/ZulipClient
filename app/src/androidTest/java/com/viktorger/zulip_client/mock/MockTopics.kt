package com.viktorger.zulip_client.mock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.viktorger.zulip_client.util.AssetsUtil

class MockTopics(private val wireMockServer: WireMockServer) {
    private val matcher = WireMock.get(urlPattern)

    fun withTopics() {
        wireMockServer.stubFor(
            matcher
                .willReturn(WireMock.ok(AssetsUtil.fromAssets("streams/topics.json")))
        )
    }

    companion object {
        val urlPattern = WireMock.urlPathMatching("/users/me/.+/topics.*")
        fun WireMockServer.topics(block: MockTopics.() -> Unit) {
            MockTopics(this).apply(block)
        }
    }
}