package com.viktorger.zulip_client.mock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.viktorger.zulip_client.util.AssetsUtil

class MockOwnProfile(private val wireMockServer: WireMockServer) {
    private val matcher = WireMock.get(urlPattern)

    fun withOwnProfile() {
        wireMockServer.stubFor(
            matcher
                .willReturn(WireMock.ok(AssetsUtil.fromAssets("chat/ownProfile.json")))
        )
    }

    companion object {
        val urlPattern = WireMock.urlPathMatching("/users/me")
        fun WireMockServer.ownProfile(block: MockOwnProfile.() -> Unit) {
            MockOwnProfile(this).apply(block)
        }
    }
}