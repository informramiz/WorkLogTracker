package github.informramiz.timetracker.presentation.createuser

import github.informramiz.timetracker.domain.authentication.model.UserRole
import github.informramiz.timetracker.presentation.base.ViewState

data class CreateUserViewState(
    val currentUserRole: UserRole,
    val newUserRole: UserRole,
    val isLoading: Boolean
) : ViewState()
