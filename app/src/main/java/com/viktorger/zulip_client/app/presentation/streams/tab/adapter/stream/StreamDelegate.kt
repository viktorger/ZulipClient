package com.viktorger.zulip_client.app.presentation.streams.tab.adapter.stream

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viktorger.zulip_client.app.R
import com.viktorger.zulip_client.app.core.ui.adapters.AdapterDelegate
import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem
import com.viktorger.zulip_client.app.core.ui.model.StreamModelUi
import com.viktorger.zulip_client.app.databinding.ItemStreamBinding

class StreamDelegate(
    private val onClick: (streamName: String) -> Unit
) : AdapterDelegate() {
    override fun onCreateHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        ViewHolder(
            ItemStreamBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DelegateItem) =
        (holder as ViewHolder).bind(
            item.content() as StreamModelUi,
            onClick
        )

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: DelegateItem,
        payloads: List<DelegateItem.Payloadable>
    ) {
        val payload = payloads.firstOrNull()
        if (payload is StreamDelegateItem.ChangePayload.ExpandStateChanged) {
            (holder as ViewHolder).bindExpandedState(payload.isExpanded)
        } else {
            (holder as ViewHolder).bind(
                item.content() as StreamModelUi,
                onClick
            )
        }
    }

    override fun isOfViewType(item: DelegateItem): Boolean = item is StreamDelegateItem

    class ViewHolder(private val binding: ItemStreamBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            streamModel: StreamModelUi, onClick: (streamName: String) -> Unit
        ) {
            binding.tvStreamStream.text = binding.root.resources.getString(
                R.string.stream_template,
                streamModel.name
            )
            bindExpandedState(streamModel.isExpanded)

            binding.root.setOnClickListener {
                onClick(streamModel.name)
            }
        }

        fun bindExpandedState(isExpanded: Boolean) {
            binding.root.isSelected = isExpanded
        }
    }

}