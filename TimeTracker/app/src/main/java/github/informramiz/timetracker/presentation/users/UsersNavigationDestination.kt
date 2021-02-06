package github.informramiz.timetracker.presentation.users

import github.informramiz.timetracker.presentation.base.NavigationDestination

sealed class UsersNavigationDestination : NavigationDestination {
    object CreateNewUser : UsersNavigationDestination()
    data class EditUser(val userId: String) : UsersNavigationDestination()
    data class UserWorkLogs(val userId: String) : UsersNavigationDestination()
}
