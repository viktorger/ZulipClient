package com.viktorger.zulip_client.app.presentation.chat

import com.viktorger.zulip_client.app.core.model.EventSeparatedMessageModel
import com.viktorger.zulip_client.app.core.model.ReactionChangeModel
import com.viktorger.zulip_client.app.core.model.ReactionWithCounter
import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.core.ui.model.MessagePageTag
import com.viktorger.zulip_client.app.presentation.chat.adapter.date.DateDelegateItem
import com.viktorger.zulip_client.app.presentation.chat.adapter.date.DateModelUi
import com.viktorger.zulip_client.app.presentation.chat.adapter.message_error.MessageErrorDelegateItem
import com.viktorger.zulip_client.app.presentation.chat.adapter.message_shimmer.MessageShimmerDelegateItem
import com.viktorger.zulip_client.app.presentation.chat.adapter.received_message.MessageReceivedDelegateItem
import com.viktorger.zulip_client.app.presentation.chat.adapter.sent_message.MessageSentDelegateItem
import com.viktorger.zulip_client.app.presentation.chat.data.ChatReducerTestData
import com.viktorger.zulip_client.app.presentation.chat.data.ChatReducerTestData.Companion.REACTION_2
import com.viktorger.zulip_client.app.presentation.chat.data.ChatReducerTestData.Companion.REACTION_3
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.Locale
import java.util.TimeZone

@RunWith(JUnit4::class)
class ChatReducerTest : BehaviorSpec({
    val storedLocale = Locale.getDefault()
    beforeTest {
        mockkStatic(TimeZone::class)
        every { TimeZone.getDefault() } returns TimeZone.getTimeZone("Europe/Moscow")
        Locale.setDefault(Locale("ru"))
    }
    afterTest {
        Locale.setDefault(storedLocale)
    }
    with(ChatReducerTestData()) {
        Given("reducer") {
            val reducer = ChatReducer()
            When("PartialState is ChatLoaded") {
                val actual = reducer.reduce(stateWithChatUiLoading, partialStateDataLoaded)
                val expected = stateWithChatUiContent
                Then("should return state with chatUi is Content and its data contains all messages") {
                    actual shouldBe expected
                }
            }
            When("PartialState is ChatLoadError") {
                val throwable = Throwable()
                val partialState = ChatPartialState.ChatLoadError(throwable)
                And("state is not Content") {
                    val actual = reducer.reduce(
                        stateWithChatUiLoading,
                        partialState
                    )
                    val expected = ChatState(
                        chatUi = LceState.Error(throwable)
                    )
                    Then("should return state with chatUi Loading") {
                        actual shouldBe expected
                    }
                }
                And("state is Content") {
                    val actual = reducer.reduce(
                        stateWithChatUiContent,
                        partialState
                    )
                    val expected = stateWithChatUiContent
                    Then("should return state with chatUi Loading") {
                        actual shouldBe expected
                    }
                }
            }
            When("PartialState is ChatLoading") {
                val actual = reducer.reduce(stateWithChatUiError, ChatPartialState.ChatLoading)
                val expected = ChatState(
                    chatUi = LceState.Loading
                )
                Then("should return state with chatUi Loading") {
                    actual shouldBe expected
                }
            }

            When("PartialState is NextPageLoading") {
                And("chatUi in state is not Content") {
                    val actual = reducer.reduce(
                        stateWithChatUiError,
                        ChatPartialState.NextPageLoading
                    )
                    val expected = stateWithChatUiError
                    Then("should return given state") {
                        actual shouldBe expected
                    }
                }
                And("chatUi in state is Content") {
                    And("doesn't have MessageShimmerDelegate with Next tag in the end") {
                        val actual = reducer.reduce(
                            stateWithChatUiContent,
                            ChatPartialState.NextPageLoading
                        )
                        val expected = stateWithChatUiContentWithNextLoading
                        Then("should return given state with MessageShimmerDelegate with the Next tag in the end") {
                            actual shouldBe expected
                        }
                    }
                    And("does have MessageShimmerDelegate with Next tag in the end") {
                        val actual = reducer.reduce(
                            stateWithChatUiContentWithNextLoading,
                            ChatPartialState.NextPageLoading
                        )
                        val expected = stateWithChatUiContentWithNextLoading
                        Then("should return given state") {
                            actual shouldBe expected
                        }
                    }
                }
            }
            When("PartialState is NextPageLoaded") {
                And("chatUi in state is not Content") {
                    val actual = reducer.reduce(
                        stateWithChatUiError,
                        partialStateNextPageLoadedSameDate
                    )
                    val expected = stateWithChatUiError
                    Then("should return given state") {
                        actual shouldBe expected
                    }
                }
                And("chatUi in state is Content") {
                    And("next message has the same date") {
                        val actual = reducer.reduce(
                            stateWithChatUiContentWithNextLoading,
                            partialStateNextPageLoadedSameDate
                        )
                        val expected = ChatState(
                            chatUi = chatUiContentWithNextSentSameDate,
                            nextPageLimitReached = true
                        )
                        Then("should return state with MessageShimmerDelegate in the end changed on new message") {
                            actual shouldBe expected
                        }
                    }
                    And("next message has different date") {
                        val actual = reducer.reduce(
                            stateWithChatUiContentWithNextLoading,
                            partialStateNextPageLoaded
                        )
                        val expected = ChatState(
                            chatUi = chatUiContentWithNextSent,
                            nextPageLimitReached = true
                        )
                        Then("should return state with MessageShimmerDelegate in the end changed to new message") {
                            actual shouldBe expected
                        }
                    }
                    And("delegates list is empty not counting MessageShimmerDelegate") {
                        val state = ChatState(
                            chatUi = LceState.Content(
                                listOf(
                                    MessageShimmerDelegateItem(
                                        MessagePageTag.Next
                                    )
                                )
                            )
                        )
                        val actual = reducer.reduce(state, partialStateNextPageLoaded)
                        val expected = ChatState(
                            chatUi = LceState.Content(
                                listOf(
                                    dateDelegateBeforeNextMessageSent,
                                    MessageSentDelegateItem(nextMessageSent)
                                )
                            ),
                            nextPageLimitReached = true
                        )
                        Then("should return state with MessageShimmerDelegate in the end changed to new message") {
                            actual shouldBe expected
                        }

                    }
                }
            }
            When("PartialState is NextPageError") {
                And("chatUi in state is not Content") {
                    val actual = reducer.reduce(
                        stateWithChatUiError,
                        ChatPartialState.NextPageError
                    )
                    val expected = stateWithChatUiError
                    Then("should return given state") {
                        actual shouldBe expected
                    }
                }
                And("chatUi in state is Content") {
                    val actual = reducer.reduce(
                        stateWithChatUiContentWithNextLoading,
                        ChatPartialState.NextPageError
                    )
                    val expected = stateWithChatUiContent.copy(
                        chatUi = LceState.Content(
                            delegateList + MessageErrorDelegateItem(
                                MessagePageTag.Next
                            )
                        )
                    )
                    Then("should return given state") {
                        actual shouldBe expected
                    }
                }
            }

            When("PartialState is PreviousPageLoading") {
                And("chatUi in state is not Content") {
                    val actual = reducer.reduce(
                        stateWithChatUiError,
                        ChatPartialState.PreviousPageLoading
                    )
                    val expected = stateWithChatUiError
                    Then("should return given state") {
                        actual shouldBe expected
                    }
                }
                And("chatUi in state is Content") {
                    And("doesn't have MessageShimmerLayout with the Previous tag in the beginning") {
                        val actual = reducer.reduce(
                            stateWithChatUiContent,
                            ChatPartialState.PreviousPageLoading
                        )
                        val expected = stateWithChatUiContentWithPreviousLoading
                        Then("should return given state with MessageShimmerLayout with the Previous tag in the beginning") {
                            actual shouldBe expected
                        }
                    }
                    And("does have MessageShimmerLayout with the Previous tag in the beginning") {
                        val actual = reducer.reduce(
                            stateWithChatUiContentWithPreviousLoading,
                            ChatPartialState.PreviousPageLoading
                        )
                        val expected = stateWithChatUiContentWithPreviousLoading
                        Then("should return given state") {
                            actual shouldBe expected
                        }
                    }
                }
            }
            When("PartialState is PreviousPageLoaded") {
                And("chatUi in state is not Content") {
                    val actual = reducer.reduce(
                        stateWithChatUiError,
                        partialStatePreviousPageLoadedSameDate
                    )
                    val expected = stateWithChatUiError
                    Then("should return given state") {
                        actual shouldBe expected
                    }
                }
                And("chatUi in state is Content") {
                    And("previous message has the same date") {
                        val stateWithChatUiContentWithPreviousSameDate = LceState.Content(
                            listOf(
                                delegateList.first(),
                                MessageSentDelegateItem(newMessageSentSameDate)
                            ) + delegateList.subList(1, delegateList.size)
                        )

                        val actual = reducer.reduce(
                            stateWithChatUiContentWithPreviousLoading,
                            partialStatePreviousPageLoadedSameDate
                        )
                        val expected = ChatState(
                            chatUi = stateWithChatUiContentWithPreviousSameDate,
                            previousPageLimitReached = true
                        )
                        Then("should return state with MessageShimmerDelegate in the beginning changed to new message") {
                            actual shouldBe expected
                        }
                    }
                    And("previous message has different date") {
                        val stateWithChatUiContentWithPrevious = LceState.Content(
                            listOf(
                                DateDelegateItem(
                                    DateModelUi("1970", "27 февр.")
                                ),
                                MessageSentDelegateItem(previousMessageSent)
                            ) + delegateList
                        )

                        val actual = reducer.reduce(
                            stateWithChatUiContentWithPreviousLoading,
                            partialStatePreviousPageLoaded
                        )
                        val expected = ChatState(
                            chatUi = stateWithChatUiContentWithPrevious,
                            previousPageLimitReached = true
                        )
                        Then("should return state with MessageShimmerDelegate in the beginning changed to new message") {
                            actual shouldBe expected
                        }
                    }
                }
            }
            When("PartialState is PreviousPageError") {
                And("chatUi in state is not Content") {
                    val actual = reducer.reduce(
                        stateWithChatUiError,
                        ChatPartialState.PreviousPageError
                    )
                    val expected = stateWithChatUiError
                    Then("should return given state") {
                        actual shouldBe expected
                    }
                }
                And("chatUi in state is Content") {
                    val actual = reducer.reduce(
                        stateWithChatUiContentWithPreviousLoading,
                        ChatPartialState.PreviousPageError
                    )
                    val expected = stateWithChatUiContent.copy(
                        chatUi = LceState.Content(
                            listOf(MessageErrorDelegateItem(MessagePageTag.Previous)) + delegateList
                        )
                    )
                    Then("should return given state") {
                        actual shouldBe expected
                    }
                }
            }

            When("PartialState is NextEvents") {
                And("event is new messageSent") {
                    And("state is not Content") {
                        val partialState = ChatPartialState.NextEvents(
                            listOf(
                                EventSeparatedMessageModel(
                                    eventId = 0,
                                    sentMessage = newMessageSentSameDate,
                                )
                            )
                        )
                        val actual = reducer.reduce(
                            stateWithChatUiError,
                            partialState
                        )
                        val expected = stateWithChatUiError
                        Then("should return given state") {
                            actual shouldBe expected
                        }
                    }
                    And("state is Content") {
                        And("sent message has the same date") {
                            val partialState = ChatPartialState.NextEvents(
                                listOf(
                                    EventSeparatedMessageModel(
                                        eventId = 0,
                                        sentMessage = newMessageSentSameDate,
                                    )
                                )
                            )
                            val actual = reducer.reduce(
                                stateWithChatUiContent,
                                partialState
                            )
                            val expected = ChatState(
                                chatUi = chatUiContentWithNextSentSameDate,
                                shouldScrollNextRenderToEnd = true
                            )
                            Then("should return state with new sent message in the end") {
                                actual shouldBe expected
                            }
                        }
                        And("sent message has different date") {
                            val partialState = ChatPartialState.NextEvents(
                                listOf(
                                    EventSeparatedMessageModel(
                                        eventId = 0,
                                        sentMessage = nextMessageSent,
                                    )
                                )
                            )
                            val actual = reducer.reduce(
                                stateWithChatUiContent,
                                partialState
                            )
                            val expected = ChatState(
                                chatUi = chatUiContentWithNextSent,
                                shouldScrollNextRenderToEnd = true
                            )
                            Then("should return state with date and new sent message in the end") {
                                actual shouldBe expected
                            }
                        }
                        And("delegates list is empty") {
                            val state = ChatState(
                                chatUi = LceState.Content(listOf())
                            )
                            val partialState = ChatPartialState.NextEvents(
                                listOf(
                                    EventSeparatedMessageModel(
                                        eventId = 0,
                                        sentMessage = nextMessageSent,
                                    )
                                )
                            )
                            val actual = reducer.reduce(state, partialState)
                            val expected = ChatState(
                                chatUi = LceState.Content(
                                    listOf(
                                        dateDelegateBeforeNextMessageSent,
                                        MessageSentDelegateItem(nextMessageSent)
                                    )
                                ),
                                shouldScrollNextRenderToEnd = true
                            )
                            Then("should return state with date and new sent message in the end") {
                                actual shouldBe expected
                            }
                        }
                    }
                }

                And("event is new messageReceived") {
                    And("state is not Content") {
                        val partialState = ChatPartialState.NextEvents(
                            listOf(
                                EventSeparatedMessageModel(
                                    eventId = 0,
                                    receivedMessage = newMessageReceivedSameDate,
                                )
                            )
                        )
                        val actual = reducer.reduce(
                            stateWithChatUiError,
                            partialState
                        )
                        val expected = stateWithChatUiError
                        Then("should return given state") {
                            actual shouldBe expected
                        }
                    }
                    And("state is Content") {
                        And("sent message has the same date") {
                            val partialState = ChatPartialState.NextEvents(
                                listOf(
                                    EventSeparatedMessageModel(
                                        eventId = 0,
                                        receivedMessage = newMessageReceivedSameDate,
                                    )
                                )
                            )
                            val actual = reducer.reduce(
                                stateWithChatUiContent,
                                partialState
                            )
                            val expected = ChatState(
                                chatUi = chatUiContentWithNextReceivedSameDate
                            )
                            Then("should return state with new sent message in the end") {
                                actual shouldBe expected
                            }
                        }
                        And("sent message has different date") {
                            val partialState = ChatPartialState.NextEvents(
                                listOf(
                                    EventSeparatedMessageModel(
                                        eventId = 0,
                                        receivedMessage = nextMessageReceived,
                                    )
                                )
                            )
                            val actual = reducer.reduce(
                                stateWithChatUiContent,
                                partialState
                            )
                            val expected = ChatState(
                                chatUi = chatUiContentWithNextReceived
                            )
                            Then("should return state with new sent message in the end") {
                                actual shouldBe expected
                            }
                        }
                    }
                }

                And("event is reaction change") {
                    And("state is not Content") {
                        val partialState = ChatPartialState.NextEvents(
                            listOf(
                                EventSeparatedMessageModel(
                                    eventId = 0,
                                    reaction = ReactionChangeModel(
                                        messageId = chatModel.sentMessages.last().messageId,
                                        emoji = REACTION_2,
                                        isOwnUser = false,
                                        isAdded = true
                                    )
                                )
                            )
                        )
                        val actual = reducer.reduce(
                            stateWithChatUiError,
                            partialState
                        )
                        val expected = stateWithChatUiError
                        Then("should return given state") {
                            actual shouldBe expected
                        }
                    }
                    And("state is Content") {
                        And("message with the given messageId exists") {
                            And("message with the given messageId is sent message") {
                                val messageInWhichReactionWillBeChanged =
                                    chatModel.sentMessages.last()
                                And("reaction with same emoji exists") {
                                    And("reaction is added") {
                                        val partialState = ChatPartialState.NextEvents(
                                            listOf(
                                                EventSeparatedMessageModel(
                                                    eventId = 0,
                                                    reaction = ReactionChangeModel(
                                                        messageId = messageInWhichReactionWillBeChanged.messageId,
                                                        emoji = REACTION_2,
                                                        isOwnUser = false,
                                                        isAdded = true
                                                    )
                                                )
                                            )
                                        )
                                        val actual = reducer.reduce(
                                            stateWithChatUiContent,
                                            partialState
                                        )
                                        val expectedDelegateList = delegateList.subList(
                                            0, delegateList.size - 1
                                        ) + MessageSentDelegateItem(
                                            messageInWhichReactionWillBeChanged.copy(
                                                reactionsWithCounter = listOf(
                                                    initialReactions.first(),
                                                    initialReactions.last().copy(
                                                        count = 2
                                                    )
                                                )
                                            )
                                        )
                                        val expected = ChatState(
                                            LceState.Content(expectedDelegateList)
                                        )
                                        Then("should return state with changed second reaction on last sent message") {
                                            actual shouldBe expected
                                        }
                                    }
                                    And("reaction is not added") {
                                        val partialState = ChatPartialState.NextEvents(
                                            listOf(
                                                EventSeparatedMessageModel(
                                                    eventId = 0,
                                                    reaction = ReactionChangeModel(
                                                        messageId = messageInWhichReactionWillBeChanged.messageId,
                                                        emoji = REACTION_2,
                                                        isOwnUser = false,
                                                        isAdded = false
                                                    )
                                                )
                                            )
                                        )
                                        val actual = reducer.reduce(
                                            stateWithChatUiContent,
                                            partialState
                                        )

                                        val expectedDelegateList = delegateList.subList(
                                            0, delegateList.size - 1
                                        ) + MessageSentDelegateItem(
                                            messageInWhichReactionWillBeChanged.copy(
                                                reactionsWithCounter = listOf(
                                                    initialReactions.first()
                                                )
                                            )
                                        )
                                        val expected = ChatState(
                                            LceState.Content(expectedDelegateList)
                                        )
                                        Then("should return state without second reaction on last sent message") {
                                            actual shouldBe expected
                                        }
                                    }
                                }
                                And("reaction with same emoji doesn't exists") {
                                    val partialState = ChatPartialState.NextEvents(
                                        listOf(
                                            EventSeparatedMessageModel(
                                                eventId = 0,
                                                reaction = ReactionChangeModel(
                                                    messageId = messageInWhichReactionWillBeChanged.messageId,
                                                    emoji = REACTION_3,
                                                    isOwnUser = false,
                                                    isAdded = true
                                                )
                                            )
                                        )
                                    )

                                    val actual = reducer.reduce(
                                        stateWithChatUiContent,
                                        partialState
                                    )
                                    val expectedDelegateList = delegateList.subList(
                                        0, delegateList.size - 1
                                    ) + MessageSentDelegateItem(
                                        messageInWhichReactionWillBeChanged.copy(
                                            reactionsWithCounter = messageInWhichReactionWillBeChanged.reactionsWithCounter +
                                                    ReactionWithCounter(
                                                        emoji = REACTION_3,
                                                        count = 1,
                                                        isSelected = false
                                                    )
                                        )
                                    )
                                    val expected = ChatState(
                                        LceState.Content(expectedDelegateList)
                                    )
                                    Then("should return state with new reaction on last sent message") {
                                        actual shouldBe expected
                                    }
                                }
                            }
                            And("message with the given messageId received message") {
                                val messageInWhichReactionWillBeChanged =
                                    chatModel.receivedMessages.last()
                                And("reaction with same emoji exists") {
                                    val partialState = ChatPartialState.NextEvents(
                                        listOf(
                                            EventSeparatedMessageModel(
                                                eventId = 0,
                                                reaction = ReactionChangeModel(
                                                    messageId = messageInWhichReactionWillBeChanged.messageId,
                                                    emoji = REACTION_2,
                                                    isOwnUser = false,
                                                    isAdded = true
                                                )
                                            )
                                        )
                                    )
                                    val actual = reducer.reduce(
                                        stateWithChatUiContent,
                                        partialState
                                    )
                                    val expectedDelegateList = delegateList.subList(
                                        0, delegateList.size - 2
                                    ) + MessageReceivedDelegateItem(
                                        messageInWhichReactionWillBeChanged.copy(
                                            reactionsWithCounter = listOf(
                                                initialReactions.first(),
                                                initialReactions.last().copy(
                                                    count = 2
                                                )
                                            )
                                        )
                                    ) + delegateList.last()
                                    val expected = ChatState(
                                        LceState.Content(expectedDelegateList)
                                    )
                                    Then("should return state with changed second reaction on last received message") {
                                        actual shouldBe expected
                                    }
                                }
                                And("reaction with same emoji doesn't exists") {
                                    val partialState = ChatPartialState.NextEvents(
                                        listOf(
                                            EventSeparatedMessageModel(
                                                eventId = 0,
                                                reaction = ReactionChangeModel(
                                                    messageId = messageInWhichReactionWillBeChanged.messageId,
                                                    emoji = REACTION_3,
                                                    isOwnUser = false,
                                                    isAdded = true
                                                )
                                            )
                                        )
                                    )

                                    val actual = reducer.reduce(
                                        stateWithChatUiContent,
                                        partialState
                                    )
                                    val expectedDelegateList = delegateList.subList(
                                        0, delegateList.size - 2
                                    ) + MessageReceivedDelegateItem(
                                        messageInWhichReactionWillBeChanged.copy(
                                            reactionsWithCounter = messageInWhichReactionWillBeChanged.reactionsWithCounter +
                                                    ReactionWithCounter(
                                                        emoji = REACTION_3,
                                                        count = 1,
                                                        isSelected = false
                                                    )
                                        )
                                    ) + delegateList.last()
                                    val expected = ChatState(
                                        LceState.Content(expectedDelegateList)
                                    )
                                    Then("should return state with new reaction on last received message") {
                                        actual shouldBe expected
                                    }
                                }
                            }
                        }
                        And("message with the given messageId doesn't exist") {
                            val nonExistentMessageId = 123456789
                            val partialState = ChatPartialState.NextEvents(
                                listOf(
                                    EventSeparatedMessageModel(
                                        eventId = 0,
                                        reaction = ReactionChangeModel(
                                            messageId = nonExistentMessageId,
                                            emoji = REACTION_2,
                                            isOwnUser = false,
                                            isAdded = true
                                        )
                                    )
                                )
                            )
                            val actual = reducer.reduce(
                                stateWithChatUiContent,
                                partialState
                            )
                            val expected = stateWithChatUiContent
                            Then("should return given state") {
                                actual shouldBe expected
                            }
                        }
                    }
                }

                And("event is empty (ignored by app)") {
                    val actual = reducer.reduce(
                        stateWithChatUiContent,
                        ChatPartialState.NextEvents(listOf(EventSeparatedMessageModel(0)))
                    )
                    val expected = stateWithChatUiContent
                    Then("should return given state") {
                        actual shouldBe expected
                    }
                }
            }

            When("PartialState is ChangeShouldScrollNextRenderToEnd") {
                val changeTo = true
                val actual = reducer.reduce(
                    stateWithChatUiContent,
                    ChatPartialState.ChangeShouldScrollNextRenderToEnd(changeTo)
                )
                val expected = stateWithChatUiContent.copy(
                    shouldScrollNextRenderToEnd = changeTo
                )
                Then("should return state with changed shouldScrollNextRenderToEnd") {
                    actual shouldBe expected
                }
            }
        }
    }
})