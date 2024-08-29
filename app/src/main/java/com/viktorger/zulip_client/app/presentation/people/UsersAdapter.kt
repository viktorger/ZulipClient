package com.viktorger.zulip_client.app.presentation.people

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.viktorger.zulip_client.app.core.ui.model.UserShortcutModelUi
import com.viktorger.zulip_client.app.core.ui.model.UserStatusUi
import com.viktorger.zulip_client.app.databinding.ItemUserBinding

class UsersAdapter(private val onClick: (userId: Int) -> Unit) :
    ListAdapter<UserShortcutModelUi, UsersAdapter.ViewHolder>(PeopleAdapterItemCallback()) {

    class PeopleAdapterItemCallback : DiffUtil.ItemCallback<UserShortcutModelUi>() {
        override fun areItemsTheSame(
            oldItem: UserShortcutModelUi,
            newItem: UserShortcutModelUi
        ): Boolean = oldItem.userId == newItem.userId

        override fun areContentsTheSame(
            oldItem: UserShortcutModelUi,
            newItem: UserShortcutModelUi
        ): Boolean = oldItem == newItem

        override fun getChangePayload(
            oldItem: UserShortcutModelUi,
            newItem: UserShortcutModelUi
        ): Any {
            if (oldItem.status != newItem.status) {
                return ChangePayloads.StatusChanged(newItem.status)
            }
            return ChangePayloads.None
        }
    }

    class ViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(userShortcutModelUi: UserShortcutModelUi, onClick: (userId: Int) -> Unit) {
            with(binding) {
                tvUserUsername.text = userShortcutModelUi.username
                tvUserEmail.text = userShortcutModelUi.email
                Glide
                    .with(root)
                    .load(userShortcutModelUi.avatarUrl)
                    .into(sivUserAvatar)

                root.setOnClickListener {
                    onClick(userShortcutModelUi.userId)
                }

                bindStatus(userShortcutModelUi.status)
            }
        }

        fun bindStatus(status: UserStatusUi) {
            binding.sivUserStatus.setImageResource(status.color)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position), onClick)

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        val payload = payloads.firstOrNull()
        if (payload is ChangePayloads.StatusChanged) {
            holder.bindStatus(payload.status)
        } else {
            holder.bind(getItem(position), onClick)
        }
    }

    sealed class ChangePayloads {
        data object None : ChangePayloads()
        data class StatusChanged(val status: UserStatusUi) : ChangePayloads()
    }
}