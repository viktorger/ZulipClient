package com.viktorger.zulip_client.homework1.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.viktorger.zulip_client.homework1.common.CONTACT_ARRAY
import com.viktorger.zulip_client.homework1.common.LOCAL_BROADCAST_INTENT_FILTER

class LocalBroadcastReceiver(
    private val activityCallback: (Array<String>) -> Unit
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == LOCAL_BROADCAST_INTENT_FILTER) {
            val extra = intent.getStringArrayExtra(CONTACT_ARRAY)
            activityCallback(extra ?: arrayOf())
        }
    }


}