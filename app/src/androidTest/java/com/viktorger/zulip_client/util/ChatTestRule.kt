package com.viktorger.zulip_client.util

import androidx.test.espresso.intent.rule.IntentsRule
import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class ChatTestRule : TestRule {

    val intentsRule = IntentsRule()
    val wiremockRule = WireMockRule(8080)
    override fun apply(base: Statement?, description: Description?): Statement {
        return RuleChain.outerRule(wiremockRule)
            .around(intentsRule)
            .apply(base, description)
    }
}