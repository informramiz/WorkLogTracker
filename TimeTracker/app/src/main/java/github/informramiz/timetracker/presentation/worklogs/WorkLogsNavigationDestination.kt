package github.informramiz.timetracker.presentation.worklogs

import github.informramiz.timetracker.presentation.base.NavigationDestination

sealed class WorkLogsNavigationDestination : NavigationDestination {
    data class WorkLogScreen(val userId: String?, val workLogId: String?) : WorkLogsNavigationDestination()
}
