package github.informramiz.timetracker.data.cloudfunctions.model

import kotlinx.serialization.Serializable

@Serializable
data class GetWorkLogsRequestApiModel(
    val userId: String
)
