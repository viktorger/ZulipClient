package com.viktorger.zulip_client.app.core.common

import com.viktorger.zulip_client.app.core.common.data.UiMappersData
import com.viktorger.zulip_client.app.core.model.ChatModel
import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem
import com.viktorger.zulip_client.app.core.ui.model.mapping.toDelegateItems
import com.viktorger.zulip_client.app.presentation.chat.adapter.received_message.MessageReceivedDelegateItem
import com.viktorger.zulip_client.app.presentation.chat.adapter.sent_message.MessageSentDelegateItem
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.Locale
import java.util.TimeZone

@RunWith(JUnit4::class)
class UiMappers : BehaviorSpec({
    val storedLocale = Locale.getDefault()
    beforeTest {
        mockkStatic(TimeZone::class)
        every { TimeZone.getDefault() } returns TimeZone.getTimeZone("Europe/Moscow")
        Locale.setDefault(Locale("ru"))
    }
    afterTest {
        Locale.setDefault(storedLocale)
    }
    with(UiMappersData()) {
        Given("UiMapper") {
            val mapperFun: ChatModel.() -> List<DelegateItem> = ChatModel::toDelegateItems
            When("sent and received messages are empty") {
                val chatModel = ChatModel(emptyList(), emptyList())
                val actual = chatModel.mapperFun()
                val expected = emptyList<DelegateItem>()
                Then("should return empty list") {
                    actual shouldBe expected
                }
            }
            When("sent and received messages are not both empty") {
                val chatModel = chatModel
                val actual = chatModel.mapperFun()
                val expected = delegateList
                Then("should return empty list") {
                    actual shouldBe expected
                }
            }
            When("sent messages is empty") {
                val chatModel = ChatModel(
                    receivedMessages = chatModel.receivedMessages,
                    sentMessages = emptyList()
                )
                val actual = chatModel.mapperFun()
                val expected = listOf(
                    UiMappersData.dateDelegateItem1,
                    MessageReceivedDelegateItem(chatModel.receivedMessages[0]),
                    UiMappersData.dateDelegateItem3,
                    MessageReceivedDelegateItem(chatModel.receivedMessages[1]),
                )
                Then("should return list of date and two received messages") {
                    actual shouldBe expected
                }
            }
            When("received messages is empty") {
                val chatModel = ChatModel(
                    receivedMessages = emptyList(),
                    sentMessages = chatModel.sentMessages
                )
                val actual = chatModel.mapperFun()
                val expected = listOf(
                    UiMappersData.dateDelegateItem2,
                    MessageSentDelegateItem(chatModel.sentMessages[0]),
                    UiMappersData.dateDelegateItem4,
                    MessageSentDelegateItem(chatModel.sentMessages[1]),
                )
                Then("should return list of date and two received messages") {
                    actual shouldBe expected
                }
            }
        }
    }
})