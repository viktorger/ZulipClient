package com.viktorger.zulip_client.mock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.viktorger.zulip_client.util.AssetsUtil

class MockSubscribedStreams(private val wireMockServer: WireMockServer) {
    private val matcher = WireMock.get(urlPattern)

    fun withSubscribedStreams() {
        wireMockServer.stubFor(
            matcher
                .willReturn(WireMock.ok(AssetsUtil.fromAssets("streams/subscribedStreams.json")))
        )
    }

    companion object {
        val urlPattern = WireMock.urlPathMatching("/users/me/subscriptions")
        fun WireMockServer.subscribedStreams(block: MockSubscribedStreams.() -> Unit) {
            MockSubscribedStreams(this).apply(block)
        }
    }
}