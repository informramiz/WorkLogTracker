package github.informramiz.timetracker.data.cloudfunctions.model

import kotlinx.serialization.Serializable

@Serializable
data class GetUserWorkingHoursResponseApiModel(val preferredWorkingHours: Float? = null)
