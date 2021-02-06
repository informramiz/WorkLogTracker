package github.informramiz.timetracker.presentation.home

import github.informramiz.timetracker.presentation.base.NavigationDestination

sealed class HomeNavigationDestination : NavigationDestination {
    object Login : HomeNavigationDestination()
}
