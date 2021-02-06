package github.informramiz.timetracker.presentation.worklogs

import github.informramiz.timetracker.presentation.base.ViewState
import github.informramiz.timetracker.presentation.settings.WorkLogUiModel

data class WorkLogsViewState(
    val preferredWorkingHours: Float?,
    val workLogs: List<WorkLogUiModel>,
    val filter: DateFilterUiModel?,
    val isRefreshing: Boolean,
    val isLoading: Boolean
) : ViewState()
