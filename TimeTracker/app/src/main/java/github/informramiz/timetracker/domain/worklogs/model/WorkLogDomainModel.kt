package github.informramiz.timetracker.domain.worklogs.model

import github.informramiz.timetracker.data.worklogs.model.WorkLogApiModel

data class WorkLogDomainModel(
    val tasksText: String,
    val hoursWorked: Float,
    val date: Long,
    val workLogId: String = ""
)

fun WorkLogDomainModel.toApiModel() = WorkLogApiModel(
    workLogId = workLogId,
    tasksText = tasksText,
    hoursWorked = hoursWorked,
    date = date
)

fun WorkLogApiModel.toDomainModel() = WorkLogDomainModel(
    workLogId = workLogId,
    tasksText = tasksText,
    hoursWorked = hoursWorked,
    date = date
)
