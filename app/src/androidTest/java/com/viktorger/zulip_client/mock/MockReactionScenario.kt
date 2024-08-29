package com.viktorger.zulip_client.mock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.viktorger.zulip_client.util.AssetsUtil

class MockReactionScenario(private val wireMockServer: WireMockServer) {
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
                .whenScenarioStateIs(STATE_REACTION_SENT)
                .willReturn(WireMock.ok(AssetsUtil.fromAssets("chat/reactionEvent.json")))
                .willSetStateTo(STATE_STARTED)
        )
    }

    fun withReaction() {
        wireMockServer.stubFor(
            matcherPost
                .willReturn(WireMock.ok())
                .willSetStateTo(STATE_REACTION_SENT)
        )
    }

    companion object {
        const val EVENT_SCENARIO = "reaction_scenario"
        const val STATE_REACTION_SENT = "state_reaction_sent"
        const val STATE_STARTED = "Started"

        val eventUrlPattern = WireMock.urlMatching("/events.*")
        val sendUrlPattern = WireMock.urlMatching("/messages/.+/reactions.*")

        fun WireMockServer.reactionScenario(block: MockReactionScenario.() -> Unit) {
            MockReactionScenario(this).apply(block)
        }
    }
}