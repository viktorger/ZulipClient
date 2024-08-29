package com.viktorger.zulip_client.app.presentation.streams

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class StreamsPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {
    private val fragments: MutableList<Fragment> = mutableListOf()

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

    @SuppressLint("NotifyDataSetChanged")
    fun update(fragments: List<Fragment>) {
        this.fragments.clear()
        this.fragments.addAll(fragments)
        notifyDataSetChanged()
    }
}