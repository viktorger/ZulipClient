package com.viktorger.zulip_client.mock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.viktorger.zulip_client.util.AssetsUtil

class MockStreamId(private val wireMockServer: WireMockServer) {
    private val matcher = WireMock.get(urlPattern)

    fun withStreamId() {
        wireMockServer.stubFor(
            matcher
                .willReturn(WireMock.ok(AssetsUtil.fromAssets("streams/streamId.json")))
        )
    }

    companion object {
        val urlPattern = WireMock.urlPathMatching("/get_stream_id.*")
        fun WireMockServer.streamId(block: MockStreamId.() -> Unit) {
            MockStreamId(this).apply(block)
        }
    }
}