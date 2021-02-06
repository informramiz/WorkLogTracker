package github.informramiz.timetracker.presentation.base

sealed class GeneralNavigationDestination : NavigationDestination {
    object Back : GeneralNavigationDestination()
    object FinishActivity : GeneralNavigationDestination()
    object RelaunchActivity : GeneralNavigationDestination()
    object LoginScreen : GeneralNavigationDestination()
}
