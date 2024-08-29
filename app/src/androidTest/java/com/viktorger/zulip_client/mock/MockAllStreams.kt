package com.viktorger.zulip_client.mock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.viktorger.zulip_client.util.AssetsUtil.fromAssets

class MockAllStreams(private val wireMockServer: WireMockServer) {
    private val matcher = WireMock.get(urlPattern)

    fun withAllStreams() {
        wireMockServer.stubFor(
            matcher
                .willReturn(WireMock.ok(fromAssets("streams/allStreams.json")))
        )
    }

    companion object {
        val urlPattern = WireMock.urlPathMatching("/streams.*")
        fun WireMockServer.allStreams(block: MockAllStreams.() -> Unit) {
            MockAllStreams(this).apply(block)
        }
    }
}