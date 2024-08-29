package com.viktorger.zulip_client.app.presentation.chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.viktorger.zulip_client.app.R
import com.viktorger.zulip_client.app.core.common.LOAD_POSITION
import com.viktorger.zulip_client.app.core.common.getAssociatedColor
import com.viktorger.zulip_client.app.core.common.lazyUnsafe
import com.viktorger.zulip_client.app.core.common.showErrorSnackBar
import com.viktorger.zulip_client.app.core.common.toast
import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.databinding.FragmentChatBinding
import com.viktorger.zulip_client.app.databinding.LayoutDialogEmojiBinding
import com.viktorger.zulip_client.app.di.component.DaggerChatComponent
import com.viktorger.zulip_client.app.presentation.app.ZulipApplication
import com.viktorger.zulip_client.app.presentation.app.appComponent
import com.viktorger.zulip_client.app.presentation.chat.adapter.MessageAdapter
import com.viktorger.zulip_client.app.presentation.chat.adapter.date.DateDelegate
import com.viktorger.zulip_client.app.presentation.chat.adapter.message_error.MessageErrorDelegate
import com.viktorger.zulip_client.app.presentation.chat.adapter.message_shimmer.MessageShimmerDelegate
import com.viktorger.zulip_client.app.presentation.chat.adapter.message_shimmer.MessageShimmerDelegateItem
import com.viktorger.zulip_client.app.presentation.chat.adapter.received_message.MessageReceivedDelegate
import com.viktorger.zulip_client.app.presentation.chat.adapter.sent_message.MessageSentDelegate
import com.viktorger.zulip_client.app.presentation.mvi.BaseFragmentMvi
import com.viktorger.zulip_client.app.presentation.mvi.MviStore
import javax.inject.Inject


class ChatFragment : BaseFragmentMvi<
        ChatPartialState,
        ChatIntent,
        ChatState,
        ChatEffect>() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazyUnsafe {
        MessageAdapter(
            loadNextPage = {
                if (!store.uiState.value.nextPageLimitReached) {
                    store.postIntent(ChatIntent.LoadNextPage(streamName, topicName))
                }
            },
            loadPreviousPage = {
                if (!store.uiState.value.previousPageLimitReached) {
                    store.postIntent(ChatIntent.LoadPreviousPage(streamName, topicName))
                }
            },
            loadPosition = LOAD_POSITION
        )
    }
    private val emojiDialog by lazyUnsafe { initBottomSheetDialog() }
    private val streamName get() = requireArguments().getString(ARG_STREAM_NAME)!!
    private val topicName get() = requireArguments().getString(ARG_TOPIC_NAME)!!

    private val router = ZulipApplication.INSTANCE.router

    @Inject
    lateinit var storeFactory: ChatStore.Factory

    override val store: MviStore<ChatPartialState, ChatIntent, ChatState, ChatEffect>
            by viewModels { storeFactory }

    override fun onAttach(context: Context) {
        val chatComponent = DaggerChatComponent.factory().create(appComponent)
        chatComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            store.postIntent(ChatIntent.LoadSavedChat(streamName, topicName))
        }
        setupTopBars()
        initRecycler()
        setupNavIcon()
        initMessageField()
        initActionButton()
    }

    override fun resolveEffect(effect: ChatEffect) {
        when (effect) {
            is ChatEffect.ChatLoadError -> {
                showErrorSnackBar(binding.root) {
                    store.postIntent(ChatIntent.LoadSavedChat(streamName, topicName))
                }
            }

            ChatEffect.SendMessageError -> toast(
                requireContext(),
                getString(R.string.message_error)
            )

            ChatEffect.ChangeReactionError -> toast(
                requireContext(),
                getString(R.string.reaction_error)
            )

            ChatEffect.EventError -> {
                showErrorSnackBar(binding.root) {
                    store.postIntent(ChatIntent.CatchEvents(streamName, topicName))
                }
            }

            ChatEffect.PageLoadError -> toast(
                requireContext(),
                getString(R.string.message_page_error)
            )

            ChatEffect.NavigateBack -> router.exit()
        }
    }

    override fun render(state: ChatState) {
        val chatUi = state.chatUi
        binding.rvChat.isVisible = chatUi is LceState.Content
        binding.pbChat.isVisible = chatUi is LceState.Loading
        if (chatUi is LceState.Content) {
            val previousLastIndex = adapter.itemCount - 1
            val messagesCountChanged = adapter.itemCount != chatUi.data.size
                    && chatUi.data.last() !is MessageShimmerDelegateItem

            adapter.submitList(chatUi.data) {
                adapterScrollSubmitCallback(
                    shouldScrollNextRenderToEnd = state.shouldScrollNextRenderToEnd,
                    nextPageLimitReached = state.nextPageLimitReached,
                    previousLastIndex = previousLastIndex,
                    messagesCountChanged = messagesCountChanged
                )
            }
        }
    }

    private fun adapterScrollSubmitCallback(
        shouldScrollNextRenderToEnd: Boolean,
        nextPageLimitReached: Boolean,
        previousLastIndex: Int,
        messagesCountChanged: Boolean
    ) {
        if (_binding == null) {
            return
        }
        val layoutManager = binding.rvChat.layoutManager as LinearLayoutManager
        val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

        if (shouldScrollNextRenderToEnd
            || (lastVisibleItem == previousLastIndex && messagesCountChanged)
        ) {
            binding.rvChat.smoothScrollToPosition(adapter.itemCount)
            if (nextPageLimitReached && shouldScrollNextRenderToEnd) {
                store.postIntent(ChatIntent.SetShouldScrollNextRenderToEndFalse)
            }
        }
    }

    override fun onDestroyView() {
        unsetupTopBars()
        _binding = null
        super.onDestroyView()
    }

    private fun setupNavIcon() {
        binding.tbChat.setNavigationIcon(R.drawable.ic_back)
        binding.tbChat.setNavigationOnClickListener {
            store.postIntent(ChatIntent.NavigateBack)
        }
    }

    private fun setupTopBars() {
        binding.tbChat.title = getString(R.string.stream_template, streamName)
        binding.tvChatTopic.text = getString(R.string.topic_template, topicName)
        val topicColor = getAssociatedColor(topicName)
        binding.tbChat.setBackgroundColor(topicColor)
        requireActivity().window.statusBarColor = topicColor
    }

    private fun unsetupTopBars() {
        requireActivity().window.statusBarColor = requireContext().resources.getColor(
            R.color.status_bar_color,
            requireActivity().theme
        )
    }

    private fun initActionButton() {
        binding.ibChatAction.setOnClickListener {
            when (it.tag) {
                ACTION_ADD -> Unit
                ACTION_SEND -> {
                    if (!binding.etChat.text.isNullOrBlank()) {
                        store.postIntent(
                            ChatIntent.SendMessage(
                                streamName = streamName,
                                topicName = topicName,
                                messageContent = binding.etChat.text.toString().trim()
                            )
                        )
                        binding.etChat.setText("")
                    }
                }
            }
        }
    }

    private fun initRecycler() {
        val onAddClick: (Int) -> Unit = { messageId ->
            emojiDialog.selectedMessageId = messageId
            emojiDialog.show()
        }
        val onEmojiClick: (Int, String, Boolean) -> Unit = { messageId, selectedEmoji, isSelected ->
            store.postIntent(ChatIntent.UpdateReaction(messageId, selectedEmoji, isSelected))
        }

        binding.rvChat.adapter = adapter
        adapter.addDelegates(
            DateDelegate(),
            MessageReceivedDelegate(
                onAddClick = onAddClick,
                onEmojiClick = onEmojiClick
            ),
            MessageSentDelegate(
                onAddClick = onAddClick,
                onEmojiClick = onEmojiClick
            ),
            MessageShimmerDelegate(),
            MessageErrorDelegate {
                store.postIntent(ChatIntent.ReloadPage(streamName, topicName, it))
            }
        )
    }

    private fun initBottomSheetDialog() = object : BottomSheetDialog(requireContext()) {
        var selectedMessageId = UNSELECTED_MESSAGE
    }.apply {
        val bottomSheetBinding = LayoutDialogEmojiBinding.inflate(layoutInflater)
        bottomSheetBinding.epDialog.setOnEmojiPickedListener {
            store.postIntent(
                ChatIntent.AddReaction(
                    messageId = selectedMessageId,
                    emoji = it.emoji
                )
            )
            dismiss()
        }

        setContentView(bottomSheetBinding.root)
    }

    private fun initMessageField() {
        var isNullOrEmpty = true
        binding.ibChatAction.tag = ACTION_ADD
        binding.etChat.doAfterTextChanged {
            if (it.isNullOrBlank()) {
                if (!isNullOrEmpty) {
                    binding.ibChatAction.setImageResource(R.drawable.ic_add_file)
                    binding.ibChatAction.tag = ACTION_ADD
                    isNullOrEmpty = true
                }
            } else {
                if (isNullOrEmpty) {
                    binding.ibChatAction.setImageResource(R.drawable.ic_send)
                    binding.ibChatAction.tag = ACTION_SEND
                    isNullOrEmpty = false
                }
            }
        }
    }


    companion object {
        private const val UNSELECTED_MESSAGE = -1

        const val ACTION_ADD = "action_add"
        const val ACTION_SEND = "action_send"

        const val ARG_STREAM_NAME = "stream_name"
        const val ARG_TOPIC_NAME = "topic_name"

        fun newInstance(streamName: String, topicName: String): ChatFragment {
            val fragment = ChatFragment()
            val arguments = Bundle()
            arguments.putString(ARG_STREAM_NAME, streamName)
            arguments.putString(ARG_TOPIC_NAME, topicName)
            fragment.arguments = arguments
            return fragment
        }
    }
}