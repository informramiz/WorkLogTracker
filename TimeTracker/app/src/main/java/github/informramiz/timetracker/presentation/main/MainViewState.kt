package github.informramiz.timetracker.presentation.main

import github.informramiz.timetracker.domain.authentication.model.UserRole
import github.informramiz.timetracker.presentation.base.ViewState

data class MainViewState(
    val isUserLoggedIn: Boolean,
    val userRole: UserRole
) : ViewState()
