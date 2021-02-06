package github.informramiz.timetracker.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEachIndexed
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import github.informramiz.timetracker.databinding.HomeFragmentBinding
import github.informramiz.timetracker.extensions.showToast
import github.informramiz.timetracker.presentation.base.NavigationDestination
import github.informramiz.timetracker.presentation.home.HomeNavigationDestination
import github.informramiz.timetracker.presentation.home.HomeViewModel
import github.informramiz.timetracker.presentation.home.HomeViewState
import github.informramiz.timetracker.view.base.BaseFragment

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeViewState>() {
    private lateinit var viewBinding: HomeFragmentBinding
    override val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return HomeFragmentBinding.inflate(inflater, container, false).also { viewBinding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.homeLogoutTextView.setOnClickListener {
            viewModel.onLogoutClickAction()
        }

    }

    override fun renderViewState(viewState: HomeViewState) {
        super.renderViewState(viewState)
        viewBinding.homeWelcomeTextView.text = viewState.userDisplayName
        viewBinding.homeRoleTextView.text = viewState.userRole.toString()
        viewBinding.homeWorkingHoursCountTextView.text = viewState.preferredWorkingHoursCountPerDay.toString()
        viewBinding.homeProgressBar.progressBar.isVisible = viewState.isLoading
    }

    override fun navigateToDestination(destination: NavigationDestination) {
        when(destination) {
            is HomeNavigationDestination.Login -> navController.navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
            else -> super.navigateToDestination(destination)
        }

    }
}
