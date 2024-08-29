package com.viktorger.zulip_client.app.core.ui.model.mapping

import com.viktorger.zulip_client.app.core.model.StreamModel
import com.viktorger.zulip_client.app.core.ui.model.StreamModelUi

internal fun StreamModel.toUi(): StreamModelUi = StreamModelUi(
    name = name,
    isExpanded = false
)