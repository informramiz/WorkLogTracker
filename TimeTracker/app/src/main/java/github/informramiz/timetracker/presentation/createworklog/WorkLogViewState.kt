package github.informramiz.timetracker.presentation.createworklog

import github.informramiz.timetracker.presentation.base.ViewState

data class WorkLogViewState(
    val tasksText: String,
    val hoursWorked: Float,
    val date: Long,
    val isLoading: Boolean
) : ViewState()
