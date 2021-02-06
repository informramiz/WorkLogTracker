package github.informramiz.timetracker.presentation.users

import github.informramiz.timetracker.domain.authentication.model.UserInfoDomainModel
import github.informramiz.timetracker.domain.authentication.model.UserRole
import github.informramiz.timetracker.presentation.base.ViewState

data class UsersViewState(
    val loggedInUserRole: UserRole,
    val users: List<UserInfoDomainModel>,
    val isLoading: Boolean,
    val isRefreshing: Boolean
) : ViewState()
