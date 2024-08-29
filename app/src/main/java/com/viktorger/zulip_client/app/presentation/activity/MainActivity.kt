package com.viktorger.zulip_client.app.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.viktorger.zulip_client.app.R
import com.viktorger.zulip_client.app.core.common.lazyUnsafe
import com.viktorger.zulip_client.app.databinding.ActivityMainBinding
import com.viktorger.zulip_client.app.presentation.navigation.Screens.TopNavigation
import com.viktorger.zulip_client.app.presentation.app.ZulipApplication


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val navigator = AppNavigator(this, R.id.fcvMain)
    private val navigatorHolder by lazyUnsafe {(application as ZulipApplication).navigatorHolder}
    private val router by lazyUnsafe { (application as ZulipApplication).router }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            router.newRootScreen(TopNavigation())
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }
}