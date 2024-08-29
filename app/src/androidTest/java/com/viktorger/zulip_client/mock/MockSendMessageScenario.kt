package com.viktorger.zulip_client.mock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.viktorger.zulip_client.util.AssetsUtil

class MockSendMessageScenario(private val wireMockServer: WireMockServer) {
    private val matcherGet = WireMock.get(eventUrlPattern)
        .inScenario(EVENT_SCENARIO)

    private val matcherPost = WireMock.post(sendUrlPattern)
        .inScenario(EVENT_SCENARIO)

    fun withEvent() {
        wireMockServer.stubFor(
            matcherGet
                .whenScenarioStateIs(STATE_STARTED)
                .willReturn(WireMock.ok(AssetsUtil.fromAssets("chat/heartbeatEvent.json")))
        )

        wireMockServer.stubFor(
            matcherGet
                .whenScenarioStateIs(STATE_MESSAGE_SENT)
                .willReturn(WireMock.ok(AssetsUtil.fromAssets("chat/messageSentEvent.json")))
                .willSetStateTo(STATE_STARTED)
        )
    }

    fun withSendMessage() {
        wireMockServer.stubFor(
            matcherPost
                .willReturn(WireMock.ok())
                .willSetStateTo(STATE_MESSAGE_SENT)
        )
    }

    companion object {
        const val EVENT_SCENARIO = "event_scenario"
        const val STATE_MESSAGE_SENT = "state_message_sent"
        const val STATE_STARTED = "Started"

        val eventUrlPattern = WireMock.urlMatching("/events.*")
        val sendUrlPattern = WireMock.urlMatching("/messages.*content.*")

        fun WireMockServer.sendMessageScenario(block: MockSendMessageScenario.() -> Unit) {
            MockSendMessageScenario(this).apply(block)
        }
    }
}