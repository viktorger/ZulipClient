package com.viktorger.zulip_client.test

import android.content.Intent
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.viktorger.zulip_client.app.R
import com.viktorger.zulip_client.app.presentation.activity.MainActivity
import com.viktorger.zulip_client.app.presentation.chat.ChatFragment
import com.viktorger.zulip_client.mock.MockAllStreams.Companion.allStreams
import com.viktorger.zulip_client.mock.MockEmptyEvent.Companion.emptyEvent
import com.viktorger.zulip_client.mock.MockMessages.Companion.messages
import com.viktorger.zulip_client.mock.MockOwnProfile.Companion.ownProfile
import com.viktorger.zulip_client.mock.MockReactionList.Companion.reactionList
import com.viktorger.zulip_client.mock.MockReactionScenario.Companion.reactionScenario
import com.viktorger.zulip_client.mock.MockRegister.Companion.register
import com.viktorger.zulip_client.mock.MockSendMessageScenario.Companion.sendMessageScenario
import com.viktorger.zulip_client.mock.MockStreamId.Companion.streamId
import com.viktorger.zulip_client.mock.MockSubscribedStreams.Companion.subscribedStreams
import com.viktorger.zulip_client.mock.MockTopics.Companion.topics
import com.viktorger.zulip_client.mock.MockUnreadMessages.Companion.unreadMessages
import com.viktorger.zulip_client.screen.ChatFragmentScreen
import com.viktorger.zulip_client.screen.StreamsFragmentScreen
import com.viktorger.zulip_client.util.ChatTestRule
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatTest : TestCase() {

    @get:Rule
    val rule = ChatTestRule()

    @Test
    fun testChatUi() {
        before {
            rule.wiremockRule.messages { withMessages() }
            rule.wiremockRule.ownProfile { withOwnProfile() }
            rule.wiremockRule.register { withRegister() }
            rule.wiremockRule.emptyEvent {
                withEvent()
            }
        }.after {
            rule.wiremockRule.resetAll()
        }.run {
            val givenStreamName = "general"
            val givenTopicName = "(no topic)"
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val chatArgs = bundleOf(
                ChatFragment.ARG_STREAM_NAME to givenStreamName,
                ChatFragment.ARG_TOPIC_NAME to givenTopicName,
            )
            FragmentScenario.launchInContainer(
                ChatFragment::class.java,
                chatArgs,
                R.style.Theme_ZulipClient
            )
            ChatFragmentScreen {
                step("Check stream and topic name display") {
                    toolbar.isVisible()
                    topicName.isVisible()
                    toolbar.hasTitle(context.getString(R.string.stream_template, givenStreamName))
                    topicName.hasText(context.getString(R.string.topic_template, givenTopicName))
                }
                step("check messages are loaded") {
                    progressBar.isGone()
                    recycler.isVisible()
                }
                step("check last message and its reaction") {
                    recycler.lastChild<ChatFragmentScreen.KSentMessageItem> {
                        isVisible()
                        hasDescendant { withText("Test") }
                        firstReaction.hasCount(1)
                    }
                }
                step("check messageField") {
                    messageField.isVisible()
                    messageField.hasHint(R.string.type_hint)
                }
                step("Check if image button has plus tag") {
                    actionButton.isVisible()
                    actionButton.hasTag(ChatFragment.ACTION_ADD)
                }
            }
        }
    }

    @Test
    fun checkChatArguments() {
        val givenStreamName = "general"
        val givenTopicName = "(no topic)"
        val chatArgs = bundleOf(
            ChatFragment.ARG_STREAM_NAME to givenStreamName,
            ChatFragment.ARG_TOPIC_NAME to givenTopicName,
        )
        val scenario = FragmentScenario.launchInContainer(
            ChatFragment::class.java,
            chatArgs,
            R.style.Theme_ZulipClient
        )
        scenario.onFragment {
            val args = it.requireArguments()
            val actualStreamName = args.getString(ChatFragment.ARG_STREAM_NAME)
            val actualTopicName = args.getString(ChatFragment.ARG_TOPIC_NAME)
            assertEquals(actualStreamName, givenStreamName)
            assertEquals(actualTopicName, givenTopicName)
        }
    }

    @Test
    fun openChatTest() = before {
        rule.wiremockRule.allStreams { withAllStreams() }
        rule.wiremockRule.subscribedStreams { withSubscribedStreams() }
        rule.wiremockRule.topics { withTopics() }
        rule.wiremockRule.streamId { withStreamId() }
        rule.wiremockRule.unreadMessages { withUnreadMessages() }

        rule.wiremockRule.messages { withMessages() }
        rule.wiremockRule.ownProfile { withOwnProfile() }
        rule.wiremockRule.register { withRegister() }
        rule.wiremockRule.emptyEvent { withEvent() }
    }.after {
        rule.wiremockRule.resetAll()
    }.run {
        val scenario = ActivityScenario.launch<MainActivity>(getMainActivityIntent())
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val givenStreamName = "general"
        val givenTopicName = "(no topic)"
        StreamsFragmentScreen {
            step("click on stream") {
                recycler.childAt<StreamsFragmentScreen.KStreamItem>(0) {
                    streamName.hasText(context.getString(R.string.stream_template, givenStreamName))
                    click()
                }
            }
            step("click on topic") {
                recycler.childWith<StreamsFragmentScreen.KTopicItem> {
                    withDescendant {
                        withText(givenTopicName)
                    }
                }.perform {
                    click()
                }
            }
        }
        ChatFragmentScreen {
            step("check items engagement") {
                toolbar.hasTitle(context.getString(R.string.stream_template, givenStreamName))
                topicName.hasText(context.getString(R.string.topic_template, givenTopicName))
                progressBar.isGone()
                recycler.isVisible()
            }
        }
        scenario.close()
    }

    @Test
    fun testSendMessage() = before {
        rule.wiremockRule.messages { withMessages() }
        rule.wiremockRule.ownProfile { withOwnProfile() }
        rule.wiremockRule.register { withRegister() }
        rule.wiremockRule.sendMessageScenario {
            withEvent()
            withSendMessage()
        }
    }.after {
        rule.wiremockRule.resetAll()
    }.run {
        val givenStreamName = "general"
        val givenTopicName = "(no topic)"
        val chatArgs = bundleOf(
            ChatFragment.ARG_STREAM_NAME to givenStreamName,
            ChatFragment.ARG_TOPIC_NAME to givenTopicName,
        )
        FragmentScenario.launchInContainer(
            ChatFragment::class.java,
            chatArgs,
            R.style.Theme_ZulipClient
        )
        ChatFragmentScreen {
            step("Check that chat is loaded") {
                progressBar.isGone()
                recycler.isVisible()
            }
            step("Type field has hint and isn't selected") {
                messageField.isVisible()
                messageField.hasHint(R.string.type_hint)
            }
            step("Check if image button has plus tag") {
                actionButton.hasTag(ChatFragment.ACTION_ADD)
            }
            step("Type text") {
                messageField.typeText("test text")
            }
            step("Check if image button has send send tag") {
                actionButton.hasTag(ChatFragment.ACTION_SEND)
            }
            step("Send message") {
                actionButton.click()
            }
            step("Check if message sent appeared in recycler") {
                recycler.lastChild<ChatFragmentScreen.KSentMessageItem> {
                    hasDescendant { withText("test text") }
                }
            }
            step("Check if image button has plus tag") {
                actionButton.hasTag(ChatFragment.ACTION_ADD)
            }
        }
    }

    @Test
    fun testReaction() = before {
        rule.wiremockRule.messages { withMessages() }
        rule.wiremockRule.ownProfile { withOwnProfile() }
        rule.wiremockRule.register { withRegister() }
        rule.wiremockRule.reactionScenario {
            withEvent()
            withReaction()
        }
        rule.wiremockRule.reactionList { withReactionList() }
    }.after {
        rule.wiremockRule.resetAll()
    }.run {
        val givenStreamName = "general"
        val givenTopicName = "(no topic)"
        val chatArgs = bundleOf(
            ChatFragment.ARG_STREAM_NAME to givenStreamName,
            ChatFragment.ARG_TOPIC_NAME to givenTopicName,
        )
        FragmentScenario.launchInContainer(
            ChatFragment::class.java,
            chatArgs,
            R.style.Theme_ZulipClient
        )
        ChatFragmentScreen {
            step("Check that chat is loaded") {
                progressBar.isGone()
                recycler.isVisible()
            }
            step("Type field has hint and isn't selected") {
                messageField.isVisible()
                messageField.hasHint(R.string.type_hint)
            }
            step("Check reaction and add reaction") {
                recycler.lastChild<ChatFragmentScreen.KSentMessageItem> {
                    firstReaction.hasCount(1)
                    firstReaction.click()
                }
            }
            step("Check changed reaction state") {
                recycler.lastChild<ChatFragmentScreen.KSentMessageItem> {
                    firstReaction.hasCount(2)
                }
            }
        }
    }

    private fun getMainActivityIntent(): Intent {
        return Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
    }
}