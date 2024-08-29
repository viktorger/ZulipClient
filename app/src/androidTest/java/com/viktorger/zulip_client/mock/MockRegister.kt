package com.viktorger.zulip_client.mock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.viktorger.zulip_client.util.AssetsUtil

class MockRegister(private val wireMockServer: WireMockServer) {
    private val matcher = WireMock.post(urlPattern)

    fun withRegister() {
        wireMockServer.stubFor(
            matcher
                .willReturn(WireMock.ok(AssetsUtil.fromAssets("chat/registerQueue.json")))
        )
    }

    companion object {
        val urlPattern = WireMock.urlPathMatching("/register.*")
        fun WireMockServer.register(block: MockRegister.() -> Unit) {
            MockRegister(this).apply(block)
        }
    }
}