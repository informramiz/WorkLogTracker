package github.informramiz.timetracker.presentation.login

import github.informramiz.timetracker.presentation.base.NavigationDestination

sealed class LoginNavigationDestination : NavigationDestination {
    object SignIn : LoginNavigationDestination()
}
