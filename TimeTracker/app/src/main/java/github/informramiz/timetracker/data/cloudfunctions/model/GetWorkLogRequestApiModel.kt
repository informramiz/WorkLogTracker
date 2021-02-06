package github.informramiz.timetracker.data.cloudfunctions.model

import kotlinx.serialization.Serializable

@Serializable
data class GetWorkLogRequestApiModel(
    val userId: String,
    val workLogId: String
)
