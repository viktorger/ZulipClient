package com.viktorger.zulip_client.app.presentation.chat.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.viktorger.zulip_client.app.core.ui.adapters.AdapterDelegate
import com.viktorger.zulip_client.app.core.ui.adapters.DelegateAdapterItemCallback
import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem

class MessageAdapter(
    private val loadNextPage: () -> Unit,
    private val loadPreviousPage: () -> Unit,
    private val loadPosition: Int
) : ListAdapter<DelegateItem, RecyclerView.ViewHolder>(
    DelegateAdapterItemCallback()
) {
    private val delegates: MutableList<AdapterDelegate> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        delegates[viewType].onCreateHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        loadNewPages(position)
        delegates[getItemViewType(position)].onBindViewHolder(holder, getItem(position))
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        val delegate = delegates[getItemViewType(position)]
        val delegatePayloads = payloads.map { it as DelegateItem.Payloadable }

        val payload = delegatePayloads.firstOrNull()
        if (payload != DelegateItem.Payloadable.None) {
            delegate.onBindViewHolder(holder, getItem(position), delegatePayloads)
        } else {
            delegate.onBindViewHolder(holder, getItem(position))
        }

        loadNewPages(position)
    }

    private fun loadNewPages(position: Int) {
        val loadPosition = if (itemCount > this.loadPosition) {
            this.loadPosition
        } else {
            itemCount
        }

        if (position == itemCount - loadPosition) {
            loadNextPage()
        }
        if (position == loadPosition - 1) {
            loadPreviousPage()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return delegates.indexOfFirst { it.isOfViewType(currentList[position]) }
    }
    fun addDelegates(vararg delegate: AdapterDelegate) {
        delegate.forEach {
            delegates.add(it)
        }
    }
}