package com.viktorger.zulip_client.app.presentation.chat.adapter.sent_message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viktorger.zulip_client.app.core.common.addAddButton
import com.viktorger.zulip_client.app.core.common.addEmojiView
import com.viktorger.zulip_client.app.core.model.MessageSentModel
import com.viktorger.zulip_client.app.core.model.ReactionWithCounter
import com.viktorger.zulip_client.app.core.ui.adapters.AdapterDelegate
import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem
import com.viktorger.zulip_client.app.core.ui.custom_view.EmojiView
import com.viktorger.zulip_client.app.databinding.ItemMessageSentBinding

class MessageSentDelegate(
    private val onAddClick: (messageId: Int) -> Unit,
    private val onEmojiClick: (messageId: Int, selectedEmoji: String, isSelected: Boolean) -> Unit
) : AdapterDelegate() {
    override fun onCreateHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        ViewHolder(
            ItemMessageSentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DelegateItem) {
        (holder as ViewHolder).bind(item.content() as MessageSentModel, onAddClick, onEmojiClick)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: DelegateItem,
        payloads: List<DelegateItem.Payloadable>
    ) {
        val payload = payloads.firstOrNull()
        val model = item.content() as MessageSentModel
        if (payload is MessageSentDelegateItem.ChangePayload.ReactionsChanged) {
            (holder as ViewHolder).bindReactions(
                payload.reactions,
                model.messageId,
                onAddClick,
                onEmojiClick
            )
        } else {
            (holder as ViewHolder).bind(
                model,
                onAddClick,
                onEmojiClick
            )
        }
    }

    override fun isOfViewType(item: DelegateItem): Boolean = item is MessageSentDelegateItem

    class ViewHolder(
        private val binding: ItemMessageSentBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            messageModel: MessageSentModel,
            onAddClick: (messageId: Int) -> Unit,
            onEmojiClick: (messageId: Int, selectedEmoji: String, isSelected: Boolean) -> Unit
        ) {
            binding.omlMessageSent.message.text = messageModel.content

            bindReactions(
                reactions = messageModel.reactionsWithCounter,
                messageId = messageModel.messageId,
                onAddClick = onAddClick,
                onEmojiClick = onEmojiClick
            )
            binding.root.setOnLongClickListener {
                onAddClick(messageModel.messageId)
                true
            }
        }
        fun bindReactions(
            reactions: List<ReactionWithCounter>,
            messageId: Int,
            onAddClick: (position: Int) -> Unit,
            onEmojiClick: (messageId: Int, selectedEmoji: String, isSelected: Boolean) -> Unit
        ) {
            with(binding.omlMessageSent.reactions) {
                removeAllViews()
                reactions.forEach { reaction ->
                    val emojiView = addEmojiView()
                    emojiView.emoji = reaction.emoji
                    emojiView.count = reaction.count
                    emojiView.isSelected = reaction.isSelected
                    emojiView.setOnClickListener {
                        onEmojiClick(
                            messageId,
                            (it as EmojiView).emoji,
                            reaction.isSelected
                        )
                    }
                }

                if (reactions.isNotEmpty()) {
                    val imageButton = addAddButton()
                    imageButton.setOnClickListener {
                        onAddClick(messageId)
                    }
                }
            }
        }
    }
}