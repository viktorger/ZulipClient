package com.viktorger.zulip_client.app.presentation.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.viktorger.zulip_client.app.R
import com.viktorger.zulip_client.app.databinding.FragmentTabsBinding
import com.viktorger.zulip_client.app.presentation.navigation.Screens.Channels
import com.viktorger.zulip_client.app.presentation.navigation.Screens.People
import com.viktorger.zulip_client.app.presentation.navigation.Screens.Profile
import com.viktorger.zulip_client.app.presentation.navigation.TaggedScreens

class TabsFragment : Fragment() {

    private var _binding: FragmentTabsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            selectTab(TaggedScreens.Channels)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initNavigation()
    }

    private fun initNavigation() {
        binding.bnvTabs.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.fragmentChannels -> {
                    selectTab(TaggedScreens.Channels)
                    true
                }

                R.id.fragmentPeople -> {
                    selectTab(TaggedScreens.People)
                    true
                }

                R.id.fragmentProfile -> {
                    selectTab(TaggedScreens.Profile)
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    private fun selectTab(tab: TaggedScreens) {
        var currentFragment: Fragment? = null
        childFragmentManager.fragments.forEach { fragment ->
            if (fragment.isVisible && fragment.tag != this.tag) {
                currentFragment = fragment
                return@forEach
            }
        }

        val newFragment = childFragmentManager.findFragmentByTag(tab.tag)
        if (currentFragment != null && newFragment != null && currentFragment === newFragment) return

        val transaction = childFragmentManager.beginTransaction()
        if (newFragment == null) {
            when (tab) {
                TaggedScreens.Channels -> transaction.add(
                    R.id.fcvTabs,
                    Channels().createFragment(childFragmentManager.fragmentFactory),
                    tab.tag
                )
                TaggedScreens.People -> transaction.add(
                    R.id.fcvTabs,
                    People().createFragment(childFragmentManager.fragmentFactory),
                    tab.tag
                )
                TaggedScreens.Profile -> transaction.add(
                    R.id.fcvTabs,
                    Profile().createFragment(childFragmentManager.fragmentFactory),
                    tab.tag
                )
            }
        }

        if (currentFragment != null) {
            transaction.hide(currentFragment!!)
        }
        if (newFragment != null) {
            transaction.show(newFragment)
        }
        transaction.commitNow()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}