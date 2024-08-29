package com.viktorger.zulip_client.app.presentation.chat

import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.presentation.chat.data.ChatActorTestData
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class ChatActorTest : BehaviorSpec({
    val unconfinedTestDispatcher = UnconfinedTestDispatcher()

    with(ChatActorTestData()) {
        Given("actor") {
            val streamName = ""
            val topicName = ""
            When("ChatIntent is LoadSavedChat") {
                And("repository doesn't throw") {
                    val actor = createSuccessfulActor()
                    val actual = actor.resolve(
                        ChatIntent.LoadSavedChat(streamName, topicName),
                        ChatState(LceState.Loading)
                    ).take(3).toList()
                    Then("should return flow emitting ChatLoading, then ChatLoaded, then catching events") {
                        actual[0] shouldBe ChatPartialState.ChatLoading
                        (actual[1] is ChatPartialState.ChatLoaded) shouldBe true
                        (actual[2] is ChatPartialState.NextEvents) shouldBe true
                    }
                }
                And("repository does throw") {
                    val actor = createUnsuccessfulActor()
                    var firstEffect: ChatEffect? = null
                    val effectJob = launch(unconfinedTestDispatcher) {
                        firstEffect = actor.effects.first()
                    }
                    val actual = actor.resolve(
                        ChatIntent.LoadSavedChat(streamName, topicName),
                        ChatState(LceState.Loading)
                    ).take(2).toList()
                    effectJob.join()
                    Then("should return flow emitting ChatLoading, then emit ChatLoadError into effects") {
                        actual[0] shouldBe ChatPartialState.ChatLoading
                        (actual[1] is ChatPartialState.ChatLoadError) shouldBe true
                        (firstEffect is ChatEffect.ChatLoadError) shouldBe true
                    }
                }
            }

            When("ChatIntent is LoadNextPage") {
                And("repository doesn't throw") {
                    val actor = createSuccessfulActor()
                    val actual = actor.resolve(
                        ChatIntent.LoadNextPage(streamName, topicName),
                        stateEmptyData
                    ).take(2).toList()
                    Then("should return flow emitting NextPageLoading, then NextPageLoaded") {
                        (actual[0] is ChatPartialState.NextPageLoading) shouldBe true
                        (actual[1] is ChatPartialState.NextPageLoaded) shouldBe true
                    }
                }
                And("repository does throw") {
                    val actor = createUnsuccessfulActor()
                    var firstEffect: ChatEffect? = null
                    val effectJob = launch(unconfinedTestDispatcher) {
                        firstEffect = actor.effects.first()
                    }
                    val actual = actor.resolve(
                        ChatIntent.LoadNextPage(streamName, topicName),
                        stateEmptyData
                    ).take(2).toList()
                    effectJob.join()
                    Then("should return flow emitting NextPageLoading, then emit PageLoadError into effects") {
                        actual[0] shouldBe ChatPartialState.NextPageLoading
                        actual[1] shouldBe ChatPartialState.NextPageError
                        firstEffect shouldBe ChatEffect.PageLoadError
                    }
                }
            }

            When("ChatIntent is LoadPreviousPage") {
                And("repository doesn't throw") {
                    val actor = createSuccessfulActor()
                    val actual = actor.resolve(
                        ChatIntent.LoadPreviousPage(streamName, topicName),
                        stateEmptyData
                    ).take(2).toList()
                    Then("should return flow emitting PreviousPageLoading, then PreviousPageLoaded") {
                        (actual[0] is ChatPartialState.PreviousPageLoading) shouldBe true
                        (actual[1] is ChatPartialState.PreviousPageLoaded) shouldBe true
                    }
                }
                And("repository does throw") {
                    val actor = createUnsuccessfulActor()
                    var firstEffect: ChatEffect? = null
                    val effectJob = launch(unconfinedTestDispatcher) {
                        firstEffect = actor.effects.first()
                    }
                    val actual = actor.resolve(
                        ChatIntent.LoadPreviousPage(streamName, topicName),
                        stateEmptyData
                    ).take(2).toList()
                    effectJob.join()
                    Then("should return flow emitting PreviousPageLoading, then emit PageLoadError into effects") {
                        actual[0] shouldBe ChatPartialState.PreviousPageLoading
                        actual[1] shouldBe ChatPartialState.PreviousPageError
                        firstEffect shouldBe ChatEffect.PageLoadError
                    }
                }
            }

            When("ChatIntent is SendMessage") {
                And("repository throws") {
                    val actor = createUnsuccessfulActor()
                    var firstEffect: ChatEffect? = null
                    val effectJob = launch(unconfinedTestDispatcher) {
                        firstEffect = actor.effects.first()
                    }
                    launch(unconfinedTestDispatcher) {
                        actor.resolve(
                            ChatIntent.SendMessage(streamName, topicName, ""),
                            stateEmptyData
                        ).collect()
                    }
                    effectJob.join()
                    Then("should return flow emitting PreviousPageLoading, then emit PageLoadError into effects") {
                        firstEffect shouldBe ChatEffect.SendMessageError
                    }
                }
            }

            When("ChatIntent is AddReaction") {
                And("repository throws") {
                    val actor = createUnsuccessfulActor()
                    var firstEffect: ChatEffect? = null
                    val effectJob = launch(unconfinedTestDispatcher) {
                        firstEffect = actor.effects.first()
                    }
                    launch(unconfinedTestDispatcher) {
                        actor.resolve(
                            ChatIntent.AddReaction(0, ""),
                            stateEmptyData
                        ).collect()
                    }
                    effectJob.join()
                    Then("should return flow emitting PreviousPageLoading, then emit PageLoadError into effects") {
                        firstEffect shouldBe ChatEffect.ChangeReactionError
                    }
                }
            }

            When("ChatIntent is UpdateReaction") {
                And("repository throws") {
                    val actor = createUnsuccessfulActor()
                    var firstEffect: ChatEffect? = null
                    val effectJob = launch(unconfinedTestDispatcher) {
                        firstEffect = actor.effects.first()
                    }
                    launch(unconfinedTestDispatcher) {
                        actor.resolve(
                            ChatIntent.UpdateReaction(0, "", false),
                            stateEmptyData
                        ).collect()
                    }
                    effectJob.join()
                    Then("should return flow emitting PreviousPageLoading, then emit PageLoadError into effects") {
                        firstEffect shouldBe ChatEffect.ChangeReactionError
                    }
                }
            }

            When("ChatIntent is CatchEvents") {
                And("repository doesn't throw") {
                    val actor = createSuccessfulActor()
                    val actual = actor.resolve(
                        ChatIntent.CatchEvents(streamName, topicName),
                        stateEmptyData
                    ).take(2).toList()
                    Then("should return flow emitting NextEvents") {
                        (actual[0] is ChatPartialState.NextEvents) shouldBe true
                        (actual[1] is ChatPartialState.NextEvents) shouldBe true
                    }
                }
                And("repository does throw") {
                    val actor = createUnsuccessfulActor()
                    var firstEffect: ChatEffect? = null
                    val effectJob = launch(unconfinedTestDispatcher) {
                        firstEffect = actor.effects.first()
                    }
                    launch {
                        actor.resolve(
                            ChatIntent.CatchEvents(streamName, topicName),
                            stateEmptyData
                        ).collect()
                    }
                    effectJob.join()
                    Then("should return flow emitting EventError into effects") {
                        (firstEffect is ChatEffect.EventError) shouldBe true
                    }
                }
            }

            When("ChatIntent is SetShouldScrollNextRenderToEndFalse") {
                val actor = createSuccessfulActor()
                val actual = actor.resolve(
                    ChatIntent.SetShouldScrollNextRenderToEndFalse,
                    stateEmptyData
                ).first()
                Then("should return flow emitting ChangeShouldScrollNextRenderToEnd with false") {
                    actual shouldBe ChatPartialState.ChangeShouldScrollNextRenderToEnd(false)
                }
            }
        }
    }
})