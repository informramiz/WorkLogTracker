package github.informramiz.timetracker.data.cloudfunctions.model

import kotlinx.serialization.Serializable

@Serializable
data class GetUserWorkingHoursRequestApiModel(val userId: String)
