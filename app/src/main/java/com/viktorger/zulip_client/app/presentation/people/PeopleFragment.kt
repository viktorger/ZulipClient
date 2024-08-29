package com.viktorger.zulip_client.app.presentation.people

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.viktorger.zulip_client.app.core.common.lazyUnsafe
import com.viktorger.zulip_client.app.core.common.showErrorSnackBar
import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.databinding.FragmentPeopleBinding
import com.viktorger.zulip_client.app.di.component.DaggerPeopleComponent
import com.viktorger.zulip_client.app.presentation.app.ZulipApplication
import com.viktorger.zulip_client.app.presentation.app.appComponent
import com.viktorger.zulip_client.app.presentation.mvi.BaseFragmentMvi
import com.viktorger.zulip_client.app.presentation.mvi.MviStore
import com.viktorger.zulip_client.app.presentation.navigation.Screens.ForeignProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class PeopleFragment : BaseFragmentMvi<
        PeoplePartialState,
        PeopleIntent,
        PeopleState,
        PeopleEffect>() {

    private var _binding: FragmentPeopleBinding? = null
    private val binding get() = _binding!!

    private val router = ZulipApplication.INSTANCE.router

    private val adapter: UsersAdapter by lazyUnsafe {
        UsersAdapter {
            store.postIntent(PeopleIntent.NavigateToForeignProfile(it))
        }
    }

    @Inject
    lateinit var storeFactory: PeopleStore.Factory

    override val store: MviStore<PeoplePartialState, PeopleIntent, PeopleState, PeopleEffect>
            by viewModels { storeFactory }

    override fun onAttach(context: Context) {
        val peopleComponent = DaggerPeopleComponent.factory().create(appComponent)
        peopleComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            store.postIntent(PeopleIntent.UpdateData)
        }
    }

    private fun initSearch() {
        val searchFlow = MutableStateFlow("")
        binding.etUsersSearch.doAfterTextChanged { text ->
            searchFlow.tryEmit(text.toString())
        }

        searchFlow.debounce(500)
            .onEach {
                store.postIntent(
                    PeopleIntent.LoadUsersWithQuery(it)
                )
            }
            .launchIn(lifecycleScope)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPeopleBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        initSearch()
    }

    override fun resolveEffect(effect: PeopleEffect) {
        when (effect) {
            is PeopleEffect.ShowError -> showErrorSnackBar(binding.root) {
                store.postIntent(
                    PeopleIntent.LoadUsersWithQuery(binding.etUsersSearch.text.toString())
                )
            }

            is PeopleEffect.NavigateToForeignProfile -> {
                router.navigateTo(ForeignProfile(effect.userId))
            }

            PeopleEffect.StatusesError -> showErrorSnackBar(binding.root) {
                store.postIntent(PeopleIntent.LoadStatuses)
            }
        }
    }

    override fun render(state: PeopleState) {
        val peopleUi = state.peopleUi
        binding.rvUsers.isVisible = peopleUi is LceState.Content
        binding.shimmerUsers.root.isVisible = peopleUi == LceState.Loading
        if (peopleUi is LceState.Content) {
            adapter.submitList(peopleUi.data)
        }
    }

    private fun initRecycler() {
        binding.rvUsers.adapter = adapter
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}