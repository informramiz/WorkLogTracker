package github.informramiz.timetracker.presentation.main

import androidx.hilt.lifecycle.ViewModelInject
import github.informramiz.timetracker.domain.authentication.AuthenticationRepository
import github.informramiz.timetracker.domain.authentication.model.UserRole
import github.informramiz.timetracker.presentation.base.BaseViewModel

class MainViewModel @ViewModelInject constructor(
    private val authenticationRepository: AuthenticationRepository
) : BaseViewModel<MainViewState>() {
    override fun initialState() = MainViewState(false, UserRole.REGULAR)

    fun loadDetails() {
        super.onStart()
        launchWithErrorHandling {
            val isUserLoggedIn = authenticationRepository.isUserLoggedIn()
            if (isUserLoggedIn) {
                val role = authenticationRepository.currentUser().role
                updateState { lastState -> lastState.copy(isUserLoggedIn = isUserLoggedIn, userRole = role) }
            }
        }
    }
}
