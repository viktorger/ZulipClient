package com.viktorger.zulip_client.homework1

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.viktorger.zulip_client.homework1.common.CONTACT_ARRAY
import com.viktorger.zulip_client.homework1.common.LOCAL_BROADCAST_INTENT_FILTER
import com.viktorger.zulip_client.homework1.components.LocalBroadcastReceiver
import com.viktorger.zulip_client.homework1.components.MyService
import com.viktorger.zulip_client.homework1.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding

    private val receiver = LocalBroadcastReceiver {
        val data = Intent().putExtra(CONTACT_ARRAY, it)
        setResult(RESULT_OK, data)
        finish()
    }

    private lateinit var service: MyService
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName?, serviceBinder: IBinder?) {
            val binder = serviceBinder as MyService.LocalBinder
            this@SecondActivity.service = binder.getService()
        }

        override fun onServiceDisconnected(className: ComponentName?) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ContextCompat.registerReceiver(
            this,
            receiver,
            IntentFilter(LOCAL_BROADCAST_INTENT_FILTER),
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    override fun onStart() {
        super.onStart()

        val intent = Intent(this, MyService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()

        unbindService(connection)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }
}