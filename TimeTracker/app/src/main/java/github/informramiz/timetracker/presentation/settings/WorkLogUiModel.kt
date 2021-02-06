package github.informramiz.timetracker.presentation.settings

import github.informramiz.timetracker.domain.worklogs.model.WorkLogDomainModel

data class WorkLogUiModel(
    val tasksText: String,
    val hoursWorked: Float,
    val date: Long,
    val workLogId: String = "",
    val isUnderPreferredHoursCount: Boolean,
    val isBackgroundColorEnabled: Boolean
)

fun WorkLogUiModel.toDomainModel() = WorkLogDomainModel(
    tasksText = tasksText,
    hoursWorked = hoursWorked,
    date = date,
    workLogId = workLogId
)

fun WorkLogDomainModel.toUiModel(totalHoursOnThisDay: Float, preferredHoursCount: Float?) = WorkLogUiModel(
    tasksText = tasksText,
    hoursWorked = hoursWorked,
    date = date,
    workLogId = workLogId,
    isUnderPreferredHoursCount = preferredHoursCount != null && totalHoursOnThisDay < preferredHoursCount,
    isBackgroundColorEnabled = preferredHoursCount != null
)
