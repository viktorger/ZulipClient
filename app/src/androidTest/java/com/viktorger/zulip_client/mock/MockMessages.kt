package com.viktorger.zulip_client.mock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.viktorger.zulip_client.util.AssetsUtil

class MockMessages(private val wireMockServer: WireMockServer) {
    private val matcher = WireMock.get(urlPattern)
        .inScenario(MESSAGE_LOAD_SCENARIO)

    fun withMessages() {
        wireMockServer.stubFor(
            matcher
                .whenScenarioStateIs(STATE_STARTED)
                .willReturn(WireMock.ok(AssetsUtil.fromAssets("chat/allMessages.json")))
                .willSetStateTo(STATE_STOP_LOADING)
        )

        wireMockServer.stubFor(
            matcher
                .whenScenarioStateIs(STATE_STOP_LOADING)
                .willReturn(WireMock.ok(AssetsUtil.fromAssets("chat/emptyPage.json")))
        )
    }

    companion object {
        const val STATE_STARTED = "Started"
        const val STATE_STOP_LOADING = "stop"
        const val MESSAGE_LOAD_SCENARIO = "message_load_scenario"
        val urlPattern = WireMock.urlMatching("/messages.*narrow.*")
        fun WireMockServer.messages(block: MockMessages.() -> Unit) {
            MockMessages(this).apply(block)
        }
    }
}