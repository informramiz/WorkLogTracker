package github.informramiz.timetracker.presentation.login

import github.informramiz.timetracker.domain.authentication.model.UserRole
import github.informramiz.timetracker.presentation.base.ViewState

data class LoginViewState(
    val isUserLoggedIn: Boolean,
    val isLoading: Boolean = false
) : ViewState()
