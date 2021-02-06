package github.informramiz.timetracker.presentation.home

import github.informramiz.timetracker.domain.authentication.model.UserRole
import github.informramiz.timetracker.presentation.base.ViewState

data class HomeViewState(
    val userId: String,
    val userDisplayName: String,
    val isUserLoggedIn: Boolean,
    val userRole: UserRole,
    val preferredWorkingHoursCountPerDay: Float,
    val isLoading: Boolean
) : ViewState()
