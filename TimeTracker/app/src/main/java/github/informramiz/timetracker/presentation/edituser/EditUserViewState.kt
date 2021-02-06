package github.informramiz.timetracker.presentation.edituser

import github.informramiz.timetracker.domain.authentication.model.UserRole
import github.informramiz.timetracker.presentation.base.ViewState

data class EditUserViewState(
    val currentUserRole: UserRole,
    val role: UserRole,
    val name: String,
    val email: String,
    val isLoggedInUser: Boolean,
    val isLoading: Boolean
) : ViewState()
