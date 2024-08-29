package com.viktorger.zulip_client.app.presentation.foreign_profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.viktorger.zulip_client.app.R
import com.viktorger.zulip_client.app.core.common.showErrorSnackBar
import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.core.ui.model.ProfileModelUi
import com.viktorger.zulip_client.app.databinding.FragmentForeignProfileBinding
import com.viktorger.zulip_client.app.di.component.DaggerForeignProfileComponent
import com.viktorger.zulip_client.app.presentation.app.ZulipApplication
import com.viktorger.zulip_client.app.presentation.app.appComponent
import com.viktorger.zulip_client.app.presentation.mvi.BaseFragmentMvi
import com.viktorger.zulip_client.app.presentation.mvi.MviStore
import javax.inject.Inject

class ForeignProfileFragment : BaseFragmentMvi<
        ForeignProfilePartialState,
        ForeignProfileIntent,
        ForeignProfileState,
        ForeignProfileEffect>() {
    private var _binding: FragmentForeignProfileBinding? = null
    private val binding get() = _binding!!

    val userId get() = requireArguments().getInt(ARG_USER_ID)
    private val router = ZulipApplication.INSTANCE.router


    @Inject
    lateinit var storeFactory: ForeignProfileStore.Factory

    override val store: MviStore<ForeignProfilePartialState, ForeignProfileIntent, ForeignProfileState, ForeignProfileEffect>
            by viewModels { storeFactory }

    override fun onAttach(context: Context) {
        val foreignProfileComponent = DaggerForeignProfileComponent.factory().create(appComponent)
        foreignProfileComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForeignProfileBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            store.postIntent(ForeignProfileIntent.LoadProfile(userId))
        }
        setupNavIcon()
    }

    override fun resolveEffect(effect: ForeignProfileEffect) {
        when (effect) {
            is ForeignProfileEffect.ShowError -> {
                showErrorSnackBar(binding.root) {
                    store.postIntent(ForeignProfileIntent.LoadProfile(userId))
                }
            }

            ForeignProfileEffect.NavigateBack -> router.exit()
        }
    }

    override fun render(state: ForeignProfileState) {
        val profileUi = state.profileUi
        binding.dataForeignProfile.root.isVisible = profileUi is LceState.Content
        binding.shimmerForeignProfile.root.isVisible = profileUi == LceState.Loading
        if (profileUi is LceState.Content) {
            fillProfileData(profileUi.data)
        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupNavIcon() {
        binding.tbForeignProfile.setNavigationIcon(R.drawable.ic_back)
        binding.tbForeignProfile.setNavigationOnClickListener {
            store.postIntent(ForeignProfileIntent.NavigateBack)
        }
    }

    private fun fillProfileData(profileModelUi: ProfileModelUi) {
        with(binding.dataForeignProfile) {
            Glide.with(binding.root)
                .load(profileModelUi.avatarUrl)
                .into(sivForeignProfile)
            tvForeignProfileUsername.text = profileModelUi.username
            tvForeignProfileStatus.text = getText(profileModelUi.status.text)
            tvForeignProfileStatus.setTextColor(
                ContextCompat.getColor(requireContext(), profileModelUi.status.color)
            )
        }
    }

    companion object {
        private const val ARG_USER_ID = "user_id"
        fun newInstance(userId: Int): ForeignProfileFragment {
            val fragment = ForeignProfileFragment()
            val arguments = Bundle()
            arguments.putInt(ARG_USER_ID, userId)
            fragment.arguments = arguments
            return fragment
        }
    }
}