package com.viktorger.zulip_client.app.presentation.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.viktorger.zulip_client.app.core.common.showErrorSnackBar
import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.core.ui.model.ProfileModelUi
import com.viktorger.zulip_client.app.databinding.FragmentProfileBinding
import com.viktorger.zulip_client.app.di.component.DaggerProfileComponent
import com.viktorger.zulip_client.app.presentation.app.appComponent
import com.viktorger.zulip_client.app.presentation.mvi.BaseFragmentMvi
import com.viktorger.zulip_client.app.presentation.mvi.MviStore
import javax.inject.Inject

class ProfileFragment : BaseFragmentMvi<
        ProfilePartialState,
        ProfileIntent,
        ProfileState,
        ProfileEffect>() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var storeFactory: ProfileStore.Factory

    override val store: MviStore<ProfilePartialState, ProfileIntent, ProfileState, ProfileEffect>
            by viewModels { storeFactory }

    override fun onAttach(context: Context) {
        val profileComponent = DaggerProfileComponent.factory().create(appComponent)
        profileComponent.inject(this)
        super.onAttach(context)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun resolveEffect(effect: ProfileEffect) {
        when (effect) {
            is ProfileEffect.ShowError -> showErrorSnackBar(binding.root) {
                store.postIntent(ProfileIntent.Init)
            }
        }
    }

    override fun render(state: ProfileState) {
        val profileUi = state.profileUi
        binding.dataProfile.root.isVisible = profileUi is LceState.Content
        binding.shimmerProfile.root.isVisible = profileUi == LceState.Loading
        if (profileUi is LceState.Content) {
            fillProfileData(profileUi.data)
        }
    }

    private fun fillProfileData(profileModelUi: ProfileModelUi) {
        with(binding.dataProfile) {
            Glide.with(binding.root)
                .load(profileModelUi.avatarUrl)
                .into(sivProfile)
            tvProfileUsername.text = profileModelUi.username
            tvProfileStatus.text = getText(profileModelUi.status.text)
            tvProfileStatus.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    profileModelUi.status.color
                )
            )
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}