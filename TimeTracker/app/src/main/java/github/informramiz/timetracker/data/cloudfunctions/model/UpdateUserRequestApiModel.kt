package github.informramiz.timetracker.data.cloudfunctions.model

import github.informramiz.timetracker.data.authentication.model.UserRoleApiModel
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequestApiModel(
    val userId: String,
    val name: String,
    val email: String,
    val role: UserRoleApiModel
)
