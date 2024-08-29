package com.viktorger.zulip_client.app.presentation.streams.tab

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.viktorger.zulip_client.app.R
import com.viktorger.zulip_client.app.core.common.lazyUnsafe
import com.viktorger.zulip_client.app.core.common.showErrorSnackBar
import com.viktorger.zulip_client.app.core.common.toast
import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.databinding.FragmentChannelsTabBinding
import com.viktorger.zulip_client.app.di.component.DaggerStreamsComponent
import com.viktorger.zulip_client.app.presentation.app.ZulipApplication
import com.viktorger.zulip_client.app.presentation.app.appComponent
import com.viktorger.zulip_client.app.presentation.chat.adapter.date.DateDelegate
import com.viktorger.zulip_client.app.presentation.mvi.BaseFragmentMvi
import com.viktorger.zulip_client.app.presentation.mvi.MviStore
import com.viktorger.zulip_client.app.presentation.navigation.Screens
import com.viktorger.zulip_client.app.presentation.streams.StreamsViewModel
import com.viktorger.zulip_client.app.presentation.streams.tab.adapter.StreamTabAdapter
import com.viktorger.zulip_client.app.presentation.streams.tab.adapter.stream.StreamDelegate
import com.viktorger.zulip_client.app.presentation.streams.tab.adapter.topic.TopicDelegate
import com.viktorger.zulip_client.app.presentation.streams.tab.adapter.topic_shimmer.TopicShimmerDelegate
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class StreamsTabFragment : BaseFragmentMvi<
        StreamTabPartialState,
        StreamTabIntent,
        StreamTabState,
        StreamTabEffect>() {

    private var _binding: FragmentChannelsTabBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazyUnsafe { StreamTabAdapter() }
    private val isSubscribedTab get() = requireArguments().getBoolean(ARG_IS_SUBSCRIBED)
    private val parentViewModel: StreamsViewModel by viewModels({ requireParentFragment() })

    private val router = ZulipApplication.INSTANCE.router

    @Inject
    lateinit var storeFactory: StreamTabStore.Factory

    override val store: MviStore<StreamTabPartialState, StreamTabIntent, StreamTabState, StreamTabEffect>
            by viewModels { storeFactory }

    override fun onAttach(context: Context) {
        val streamsComponent = DaggerStreamsComponent.factory().create(appComponent)
        streamsComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            store.postIntent(StreamTabIntent.UpdateData(isSubscribedTab))
        }
        initSearchFieldListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChannelsTabBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
    }

    override fun resolveEffect(effect: StreamTabEffect) {
        when (effect) {
            is StreamTabEffect.StreamsUpdateError -> showErrorSnackBar(binding.root) {
                store.postIntent(
                    StreamTabIntent.UpdateData(isSubscribedTab)
                )
            }

            is StreamTabEffect.TopicUpdateError -> toast(
                requireContext(),
                getString(
                    R.string.topic_loading_error,
                    effect.streamName
                )
            )

            is StreamTabEffect.NavigateToChat-> {
                router.navigateTo(Screens.Chat(effect.streamName, effect.topicName))
            }
        }
    }

    override fun render(state: StreamTabState) {
        val streamsUi = state.streamsUi
        if (streamsUi is LceState.Content) {
            adapter.submitList(streamsUi.data)
        }
        binding.rvChannelsTab.isVisible = streamsUi is LceState.Content
        binding.shimmerChannelsTab.root.isVisible = streamsUi == LceState.Loading
    }

    private fun initSearchFieldListener() {
        parentViewModel.searchQueryPublisher
            .debounce(500)
            .onEach {
                store.postIntent(
                    StreamTabIntent.LoadStreamsWithQuery(
                        query = it,
                        isSubscribed = isSubscribedTab
                    )
                )
            }
            .launchIn(lifecycleScope)
    }

    private fun initRecycler() {
        val onChannelClick: (String) -> Unit = { streamName ->
            store.postIntent(StreamTabIntent.ChangeExpandedState(streamName))
        }
        val onTopicClick: (String, String) -> Unit = { streamName, topicName ->
            store.postIntent(StreamTabIntent.NavigateToChat(streamName, topicName))
        }

        binding.rvChannelsTab.adapter = adapter
        adapter.addDelegates(
            DateDelegate(),
            StreamDelegate(onChannelClick),
            TopicDelegate(onTopicClick),
            TopicShimmerDelegate()
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_IS_SUBSCRIBED = "tab_type"

        fun newInstance(isSubscribed: Boolean): StreamsTabFragment {
            val fragment = StreamsTabFragment()
            val arguments = Bundle()
            arguments.putBoolean(ARG_IS_SUBSCRIBED, isSubscribed)
            fragment.arguments = arguments
            return fragment
        }
    }

}