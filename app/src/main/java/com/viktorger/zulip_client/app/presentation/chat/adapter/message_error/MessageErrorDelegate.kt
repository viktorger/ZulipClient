package com.viktorger.zulip_client.app.presentation.chat.adapter.message_error

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viktorger.zulip_client.app.core.ui.adapters.AdapterDelegate
import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem
import com.viktorger.zulip_client.app.core.ui.model.MessagePageTag
import com.viktorger.zulip_client.app.databinding.ItemMessageErrorBinding

class MessageErrorDelegate(
    private val onRetryClick: (MessagePageTag) -> Unit
) : AdapterDelegate() {
    override fun onCreateHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        ViewHolder(
            ItemMessageErrorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DelegateItem) =
        (holder as ViewHolder).bind(
            (item.content() as MessagePageTag),
            onRetryClick
        )

    override fun isOfViewType(item: DelegateItem): Boolean = item is MessageErrorDelegateItem

    class ViewHolder(
        private val binding: ItemMessageErrorBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            messagePageTag: MessagePageTag,
            onRetryClick: (MessagePageTag) -> Unit
        ) {
            binding.btnMessageErrorRetry.setOnClickListener {
                onRetryClick(messagePageTag)
            }
        }
    }
}