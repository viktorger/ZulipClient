package com.viktorger.zulip_client.app.presentation.streams

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.viktorger.zulip_client.app.R
import com.viktorger.zulip_client.app.databinding.FragmentChannelsBinding
import com.viktorger.zulip_client.app.presentation.streams.tab.StreamsTabFragment


class StreamsFragment : Fragment() {
    private var _binding: FragmentChannelsBinding? = null
    private val binding get() = _binding!!

    private val vm: StreamsViewModel by viewModels()

    private var adapter: StreamsPagerAdapter? = null
    private val tabsTitles by lazy {
        listOf(getString(R.string.tab_subscribed), getString(R.string.tab_all_streams))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChannelsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViewPager()
        initSearch()
    }

    private fun initSearch() {
        binding.etChannelsSearch.doAfterTextChanged { text ->
            vm.searchQueryPublisher.tryEmit(text.toString())
        }
    }

    private fun initViewPager() {
        adapter = StreamsPagerAdapter(this)
        adapter?.update(listOf(
            StreamsTabFragment.newInstance(true),
            StreamsTabFragment.newInstance(false)
        ))
        with(binding) {
            vpChannels.adapter = adapter
            TabLayoutMediator(tlChannels, vpChannels) { tab, position ->
                tab.text = tabsTitles[position]
            }.attach()
        }

    }

    override fun onDestroyView() {
        adapter = null
        _binding = null
        super.onDestroyView()
    }
}