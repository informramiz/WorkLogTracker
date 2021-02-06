package github.informramiz.timetracker.presentation.home

import androidx.hilt.lifecycle.ViewModelInject
import github.informramiz.timetracker.domain.authentication.AuthenticationRepository
import github.informramiz.timetracker.domain.authentication.model.UserRole
import github.informramiz.timetracker.presentation.base.BaseViewModel
import github.informramiz.timetracker.presentation.base.GeneralNavigationDestination

class HomeViewModel @ViewModelInject constructor(
    private val authenticationRepository: AuthenticationRepository,
) : BaseViewModel<HomeViewState>() {

    override fun initialState() = HomeViewState(
        "",
        "",
        false,
        UserRole.REGULAR,
        preferredWorkingHoursCountPerDay = 8f,
        isLoading = true
    )

    override fun onStart() {
        launchWithErrorHandling {
            val isUserLoggedIn = authenticationRepository.isUserLoggedIn()
            if (isUserLoggedIn) {
                val userInfo = authenticationRepository.currentUser()
                updateState {lastState ->
                    lastState.copy(
                        userId = userInfo.userId,
                        userDisplayName = userInfo.name,
                        isUserLoggedIn = isUserLoggedIn,
                        userRole = userInfo.role,
                        isLoading = false
                    )
                }
            } else {
                updateState { lastState -> lastState.copy(isLoading = false) }
                navigateToLogin()
            }
        }
    }

    override fun onDataLoadFailure() {
        super.onDataLoadFailure()
        updateState { lastState -> lastState.copy(isLoading = false) }
    }

    private fun navigateToLogin() {
        navigate(HomeNavigationDestination.Login)
    }

    fun onLogoutClickAction() {
        launchWithErrorHandling {
            authenticationRepository.signOut()
            navigate(GeneralNavigationDestination.RelaunchActivity)
        }
    }
}
