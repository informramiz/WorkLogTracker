package github.informramiz.timetracker.presentation.settings

import github.informramiz.timetracker.presentation.base.NavigationDestination

sealed class SettingsNavigationDestination : NavigationDestination {
    data class EditUser(val userId: String) : SettingsNavigationDestination()
}
