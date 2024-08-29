package com.viktorger.zulip_client.mock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.viktorger.zulip_client.util.AssetsUtil

class MockEmptyEvent(private val wireMockServer: WireMockServer) {
    private val matcher = WireMock.get(urlPattern)

    fun withEvent() {
        wireMockServer.stubFor(
            matcher
                .willReturn(WireMock.ok(AssetsUtil.fromAssets("chat/heartbeatEvent.json")))
        )
    }

    companion object {

        val urlPattern = WireMock.urlMatching("/events.*")
        fun WireMockServer.emptyEvent(block: MockEmptyEvent.() -> Unit) {
            MockEmptyEvent(this).apply(block)
        }
    }
}