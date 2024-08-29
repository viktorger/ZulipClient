package com.viktorger.zulip_client.app.core.ui.model.mapping

import com.viktorger.zulip_client.app.core.model.MessagePageLoadAnswer
import com.viktorger.zulip_client.app.core.ui.model.MessagePageLoadAnswerUi

fun MessagePageLoadAnswer.toUi(): MessagePageLoadAnswerUi = MessagePageLoadAnswerUi(
    chat = chat.toDelegateItems(),
    limitReached = limitReached
)