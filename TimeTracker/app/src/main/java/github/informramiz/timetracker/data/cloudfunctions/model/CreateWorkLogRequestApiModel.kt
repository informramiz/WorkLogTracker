package github.informramiz.timetracker.data.cloudfunctions.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateWorkLogRequestApiModel(
    val userId: String,
    val tasksText: String,
    val hoursWorked: Float,
    val date: Long
)
