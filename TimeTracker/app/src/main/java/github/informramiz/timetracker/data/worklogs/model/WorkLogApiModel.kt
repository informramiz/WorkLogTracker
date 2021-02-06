package github.informramiz.timetracker.data.worklogs.model

import kotlinx.serialization.Serializable

@Serializable
data class WorkLogApiModel(
    val workLogId: String = "",
    val tasksText: String = "",
    val hoursWorked: Float = 0f,
    val date: Long = 0
)
