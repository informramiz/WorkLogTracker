package github.informramiz.timetracker.data.cloudfunctions.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateWorkLogRequestApiModel(
    val userId: String,
    val workLogId: String,
    val tasksText: String,
    val hoursWorked: Float,
    val date: Long
)
