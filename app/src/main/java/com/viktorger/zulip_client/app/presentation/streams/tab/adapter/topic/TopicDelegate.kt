package com.viktorger.zulip_client.app.presentation.streams.tab.adapter.topic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viktorger.zulip_client.app.R
import com.viktorger.zulip_client.app.core.ui.adapters.AdapterDelegate
import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem
import com.viktorger.zulip_client.app.core.ui.model.TopicModelUi
import com.viktorger.zulip_client.app.databinding.ItemTopicBinding

class TopicDelegate(
    private val onClick: (streamName: String, topicName: String) -> Unit
) : AdapterDelegate() {
    override fun onCreateHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        ViewHolder(
            ItemTopicBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DelegateItem) =
        (holder as ViewHolder).bind(item.content() as TopicModelUi, onClick)

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: DelegateItem,
        payloads: List<DelegateItem.Payloadable>
    ) {
        val payload = payloads.firstOrNull()
        if (payload is TopicDelegateItem.ChangePayload.MessageCountChange) {
            (holder as ViewHolder).bindMessageCount(payload.messageCount)
        } else {
            (holder as ViewHolder).bind(item.content() as TopicModelUi, onClick)
        }
    }

    override fun isOfViewType(item: DelegateItem): Boolean = item is TopicDelegateItem


    class ViewHolder(private val binding: ItemTopicBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            topicModel: TopicModelUi,
            onClick: (streamName: String, topicName: String) -> Unit
        ) {
            with(binding) {
                tvTopicName.text = topicModel.topicName
                root.setBackgroundColor(topicModel.color)
                root.setOnClickListener {
                    onClick(
                        topicModel.streamName,
                        topicModel.topicName
                    )
                }
            }
            bindMessageCount(topicModel.messagesCount)
        }
        fun bindMessageCount(messagesCount: Int) {
            binding.tvTopicMessageCount.text = binding.root.resources.getString(
                R.string.messages_count_template,
                messagesCount
            )
        }
    }
}