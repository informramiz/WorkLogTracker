package github.informramiz.timetracker.presentation.users

import androidx.hilt.lifecycle.ViewModelInject
import github.informramiz.timetracker.domain.authentication.AuthenticationRepository
import github.informramiz.timetracker.domain.authentication.model.UserRole
import github.informramiz.timetracker.domain.user.UserManagementRepository
import github.informramiz.timetracker.presentation.base.BaseViewModel

class UsersViewModel @ViewModelInject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val usersManagementRepository: UserManagementRepository
) : BaseViewModel<UsersViewState>() {
    override fun initialState() = UsersViewState(
        loggedInUserRole = UserRole.REGULAR,
        users = emptyList(),
        isLoading = true,
        isRefreshing = false
    )

    override fun onDataLoadFailure() {
        super.onDataLoadFailure()
        updateState { lastState -> lastState.copy(isLoading = false, isRefreshing = false) }
    }

    override fun onStart() {
        super.onStart()
        launchWithErrorHandling {
            val currentUserInfo = authenticationRepository.currentUser()
            val users = usersManagementRepository.getUsers()
            updateState { lastState ->
                lastState.copy(
                    loggedInUserRole = currentUserInfo.role,
                    users = users,
                    isLoading = false
                )
            }
        }
    }

    fun onCreateNewUserFabClickAction() {
        navigate(UsersNavigationDestination.CreateNewUser)
    }

    fun onSwipeToRefreshAction() {
        updateState { lastState -> lastState.copy(isRefreshing = true) }
        launchWithErrorHandling {
            val users = usersManagementRepository.getUsers()
            updateState { lastState -> lastState.copy(users = users, isRefreshing = false) }
        }
    }

    fun onUserItemClickAction(userId: String) {
        if (currentViewState().loggedInUserRole == UserRole.ADMIN) {
            notify(UsersDialogCommand.SelectAdminActionDialog(userId))
        } else {
            navigate(UsersNavigationDestination.EditUser(userId))
        }
    }

    fun onEditUserActionSelected(userId: String) {
        navigate(UsersNavigationDestination.EditUser(userId))
    }

    fun onOpenUserRecordsActionSelected(userId: String) {
        navigate(UsersNavigationDestination.UserWorkLogs(userId))
    }
}
