package com.viktorger.zulip_client.homework1.components

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.provider.ContactsContract
import com.viktorger.zulip_client.homework1.common.CONTACT_ARRAY
import com.viktorger.zulip_client.homework1.common.LOCAL_BROADCAST_INTENT_FILTER

class MyService : Service() {
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): MyService = this@MyService
    }

    override fun onBind(intent: Intent): IBinder {
        sendContactBroadcast()
        return binder
    }

    private fun sendContactBroadcast() {
        val contactList = mutableListOf<String>()

        contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        ).use { cursor ->
            cursor?.let {
                with (it) {
                    while (moveToNext()) {
                        val nameColumnIndex = getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
                        contactList.add(getString(nameColumnIndex))
                    }
                }
            }
        }

        val broadcastIntent = Intent(LOCAL_BROADCAST_INTENT_FILTER)
        broadcastIntent.putExtra(CONTACT_ARRAY, contactList.toTypedArray())
        sendBroadcast(broadcastIntent)
    }
}