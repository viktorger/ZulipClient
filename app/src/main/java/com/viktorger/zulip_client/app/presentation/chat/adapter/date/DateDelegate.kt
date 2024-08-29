package com.viktorger.zulip_client.app.presentation.chat.adapter.date

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viktorger.zulip_client.app.core.ui.adapters.AdapterDelegate
import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem
import com.viktorger.zulip_client.app.databinding.ItemDateBinding

class DateDelegate : AdapterDelegate() {
    override fun onCreateHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        ViewHolder(ItemDateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DelegateItem) =
        (holder as ViewHolder).bind(item.content() as DateModelUi)

    override fun isOfViewType(item: DelegateItem): Boolean = item is DateDelegateItem

    class ViewHolder(private val binding: ItemDateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dateModelUi: DateModelUi) {
            binding.tvDate.text = dateModelUi.date
        }
    }
}