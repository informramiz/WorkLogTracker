package github.informramiz.timetracker.data.cloudfunctions.model

import kotlinx.serialization.Serializable

@Serializable
data class DeleteWorkLogRequestApiModel(
    val userId: String,
    val workLogId: String
)
