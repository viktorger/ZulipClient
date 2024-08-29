package com.viktorger.zulip_client.app.core.ui.model.mapping

import com.viktorger.zulip_client.app.core.model.ReactionChangeModel
import com.viktorger.zulip_client.app.core.model.ReactionWithCounter

internal fun ReactionChangeModel.toReactionWithCounter() = ReactionWithCounter(
    emoji = emoji,
    count = 1,
    isSelected = isOwnUser
)