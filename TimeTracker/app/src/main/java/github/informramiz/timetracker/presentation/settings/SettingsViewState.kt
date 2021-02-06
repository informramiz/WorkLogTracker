package github.informramiz.timetracker.presentation.settings

import github.informramiz.timetracker.domain.authentication.model.UserRole
import github.informramiz.timetracker.presentation.base.ViewState

data class SettingsViewState(
    val userId: String,
    val name: String,
    val email: String,
    val userRole: UserRole,
    val workingHoursCount: Float?,
    val isLoading: Boolean = false
) : ViewState()
